from django.contrib import admin

from django.contrib import admin
from .models import Brand, FuelType, Engine, CarModel, Year, Car, SearchQuery, SearchResult

@admin.register(Brand)
class BrandAdmin(admin.ModelAdmin):
    list_display = ('name',)
    search_fields = ('name',)

@admin.register(FuelType)
class FuelTypeAdmin(admin.ModelAdmin):
    list_display = ('name',)
    search_fields = ('name',)

@admin.register(Engine)
class EngineAdmin(admin.ModelAdmin):
    list_display = ('type', 'fuel_type')
    search_fields = ('type',)

@admin.register(CarModel)
class CarModelAdmin(admin.ModelAdmin):
    list_display = ('name', 'brand')
    search_fields = ('name',)

@admin.register(Year)
class YearAdmin(admin.ModelAdmin):
    list_display = ('year',)
    ordering = ('-year',)

@admin.register(Car)
class CarAdmin(admin.ModelAdmin):
    list_display = ('model', 'year', 'engine')
    search_fields = ('model__name',)

@admin.register(SearchQuery)
class SearchQueryAdmin(admin.ModelAdmin):
    list_display = ('user', 'created_at', 'query_name')
    search_fields = ('query_name',)

@admin.register(SearchResult)
class SearchResultAdmin(admin.ModelAdmin):
    list_display = ('search_query', 'car', 'rating', 'created_at')
    search_fields = ('car__model__name',)

