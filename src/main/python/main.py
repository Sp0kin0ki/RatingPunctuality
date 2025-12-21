import pandas as pd
from utils import get_db_connection
import numpy as np
from mlxtend.frequent_patterns import apriori, association_rules
from mlxtend.preprocessing import TransactionEncoder
from scipy.sparse import csr_matrix
import asyncio


RULES_FILE = '/app/out/flight_delay_rules.csv'
        
async def async_load_data_from_db():
    """Асинхронная загрузка данных из БД"""
    async with get_db_connection() as conn:
        rows = await conn.fetch("SELECT * FROM flight_features")
        return pd.DataFrame([dict(r) for r in rows])

def prepare_transactions(df):
    transactions = []
    for _, row in df.iterrows():
        transaction = []
        for col in df.columns:
            transaction.append(f"{col}={row[col]}")
        transactions.append(transaction)
    return transactions

def format_rule(antecedents, consequents):
    condition_map = {
        'day_of_week': {
            'Понедельник': 'понедельник',
            'Вторник': 'вторник',
            'Среда': 'среда',
            'Четверг': 'четверг',
            'Пятница': 'пятница',
            'Суббота': 'суббота',
            'Воскресенье': 'воскресенье'
        },
        'time_of_day': {
            'Утро': 'утро',
            'День': 'день',
            'Вечер': 'вечер',
            'Ночь': 'ночь'
        },
        'season': {
            'Зима': 'зима',
            'Весна': 'весна',
            'Лето': 'лето',
            'Осень': 'осень'
        }
    }

    conditions = []
    delay = None

    for item in antecedents:
        col, val = item.split('=')
        if col in condition_map:
            conditions.append(condition_map[col].get(val, val))
        elif col == 'departure_airport':
            conditions.append(f'аэропорт вылета {val}')
        elif col == 'arrival_airport':
            conditions.append(f'аэропорт прилета {val}')
        elif col == 'airline_iata_code':
            conditions.append(f'авиакомпания {val}')

    for item in consequents:
        col, val = item.split('=')
        if col == 'delay_category':
            if val == 'Нет_задержки':
                delay = 'нет задержки'
            elif val == 'Короткая':
                delay = 'короткая задержка'
            elif val == 'Средняя':
                delay = 'средняя задержка'
            elif val == 'Длинная':
                delay = 'длинная задержка'
            elif val == 'Очень_длинная':
                delay = 'очень длинная задержка'

    return f"если {', '.join(conditions)}, то {delay}"

def find_delay_rules(transactions, min_support=0.05):
    te = TransactionEncoder()
    
    te_ary = te.fit(transactions).transform(transactions, sparse=True)
    
    sparse_matrix = csr_matrix(te_ary, dtype=bool)
    
    item_support = np.array(sparse_matrix.mean(axis=0)).flatten()
    
    mask = item_support >= min_support
    selected_columns = [te.columns_[i] for i in np.where(mask)[0]]
    
    filtered_matrix = sparse_matrix[:, mask]
    df_encoded = pd.DataFrame.sparse.from_spmatrix(
        filtered_matrix,
        columns=selected_columns
    )

    delay_columns = [col for col in df_encoded.columns if 'delay_category=' in col]
    other_columns = [col for col in df_encoded.columns if 'delay_category=' not in col]
    
    if len(other_columns) > 1000:
        other_columns = other_columns[:1000]
    
    df_encoded = df_encoded[delay_columns + other_columns]

    print(f"Используется {len(df_encoded.columns)} колонок после фильтрации")

    frequent_itemsets = apriori(
        df_encoded,
        min_support=min_support,
        use_colnames=True,
        low_memory=True,
        max_len=4
    )

    if not frequent_itemsets.empty:
        rules = association_rules(
            frequent_itemsets,
            metric="lift",
            min_threshold=1.5,
            support_only=False
        )

        delay_rules = rules[
            rules['consequents'].apply(
                lambda x: any('delay_category=' in item for item in x) and 
                not any('delay_category=Нет_задержки' in item for item in x)
            )
        ]

        delay_rules['formatted_rule'] = delay_rules.apply(
            lambda x: format_rule(x['antecedents'], x['consequents']),
            axis=1
        )

        return delay_rules.sort_values(by=['lift', 'confidence'], ascending=False)
    
    print("Не найдено частых наборов с заданным min_support.")
    return pd.DataFrame()

def run_analysis_task():
    """Синхронная обертка для асинхронной загрузки"""
    loop = asyncio.new_event_loop()
    asyncio.set_event_loop(loop)
    try:
        df = loop.run_until_complete(async_load_data_from_db())
        print(f"Загружено {len(df)} строк")
        
        transactions = prepare_transactions(df)
        rules = find_delay_rules(transactions, min_support=0.001)
        
        if not rules.empty:
            rules.to_csv(RULES_FILE, index=False)
            print("Результаты сохранены в flight_delay_rules.csv")
        else:
            print("Не удалось найти правила")
    except Exception as e:
        print(f"Ошибка при выполнении анализа: {str(e)}")
    finally:
        loop.close()
        
if __name__ == "__main__":
    run_analysis_task()