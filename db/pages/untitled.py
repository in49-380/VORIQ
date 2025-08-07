import pandas as pd
from django.core.management.base import BaseCommand
from pages.models import Brand, Model, Engine, FuelType, CarConfiguration

class Command(BaseCommand):
    help = 'Импорт данных из Autos.xlsx'

    def handle(self, *args, **kwargs):
        # Загрузка данных из файла
        file_path = 'Autos.xlsx'

        # Чтение данных из Excel
        brands = pd.read_excel(file_path, sheet_name=0)
        models = pd.read_excel(file_path, sheet_name=1)
        engines = pd.read_excel(file_path, sheet_name=2)
        fuel_types = pd.read_excel(file_path, sheet_name=3)
        configurations = pd.read_excel(file_path, sheet_name=4)

        # Импорт данных о брендах
        for _, row in brands.iterrows():
            Brand.objects.get_or_create(id=row['id'], name=row['name'])

        # Импорт данных о моделях
        for _, row in models.iterrows():
            brand = Brand.objects.get(id=row['brand_id'])
            Model.objects.get_or_create(id=row['id'], name=row['name'], brand=brand)

        # Импорт данных о двигателях
        for _, row in engines.iterrows():
            Engine.objects.get_or_create(id=row['id'], type=row['type'])

        # Импорт данных о типах топлива
        for _, row in fuel_types.iterrows():
            FuelType.objects.get_or_create(id=row['id'], name=row['name'])

        # Импорт конфигураций автомобилей
        for _, row in configurations.iterrows():
            model = Model.objects.get(id=row['model_id'])
            engine = Engine.objects.get(id=row['engine_id'])
            fuel_type = FuelType.objects.get(id=row['fuel_type_id'])
            CarConfiguration.objects.get_or_create(
                id=row['id'], model=model, engine=engine, fuel_type=fuel_type
            )

        self.stdout.write(self.style.SUCCESS('Данные успешно импортированы!'))