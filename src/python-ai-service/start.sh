#!/bin/bash

set -e

if [ ! -d "venv" ]; then
    echo "Creating virtual environment..."
    python -m venv venv
fi

echo "Activating virtual environment..."
source venv/Scripts/activate

echo "Installing dependencies from requirements.txt..."
pip install -r requirements.txt
echo "Starting Uvicorn server..."
uvicorn app.api:app --host 0.0.0.0 --port 8000 --reload

