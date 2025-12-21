from contextlib import asynccontextmanager
import os
import asyncpg

@asynccontextmanager
async def get_db_connection():
    conn = await asyncpg.connect(os.getenv('DB_DSN'))
    try:
        yield conn
    finally:
        await conn.close()