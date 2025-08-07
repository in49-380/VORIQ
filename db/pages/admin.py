from django.contrib import admin
from .models import Brand, CarModel, Engine, FuelType, Car, Year, SearchQuery, SearchResult


@admin.register(Brand)
class BrandAdmin(admin.ModelAdmin):
    list_display = ['id', 'name']
    search_fields = ['name']


@admin.register(FuelType)
class FuelTypeAdmin(admin.ModelAdmin):
    list_display = ['id', 'name']
    search_fields = ['name']


@admin.register(Engine)
class EngineAdmin(admin.ModelAdmin):
    list_display = ['id', 'type', 'fuel_type']
    list_filter = ['fuel_type']
    search_fields = ['type']
    autocomplete_fields = ['fuel_type']


@admin.register(CarModel)
class CarModelAdmin(admin.ModelAdmin):
    list_display = ['id', 'name', 'brand']
    list_filter = ['brand']
    search_fields = ['name', 'brand__name']
    autocomplete_fields = ['brand']


@admin.register(Year)
class YearAdmin(admin.ModelAdmin):
    list_display = ['id', 'year']
    ordering = ['-year']
    search_fields = ['year']


@admin.register(Car)
class CarAdmin(admin.ModelAdmin):
    list_display = ['id', 'model', 'year', 'engine']
    list_filter = ['model__brand', 'year', 'engine__fuel_type']
    search_fields = ['model__name', 'model__brand__name']
    autocomplete_fields = ['model', 'engine', 'year']
    list_select_related = ['model__brand', 'engine__fuel_type', 'year']


@admin.register(SearchQuery)
class SearchQueryAdmin(admin.ModelAdmin):
    list_display = ['id', 'user', 'get_criteria_summary', 'created_at']
    list_filter = ['brand', 'fuel_type', 'engine', 'model', 'year', 'created_at', 'user']
    search_fields = ['query_name', 'user__username']
    readonly_fields = ['created_at']
    autocomplete_fields = ['user', 'brand', 'fuel_type', 'engine', 'model', 'year']
    date_hierarchy = 'created_at'

    def get_criteria_summary(self, obj):
        parts = []
        if obj.brand: parts.append(f"Brand: {obj.brand}")
        if obj.fuel_type: parts.append(f"Fuel: {obj.fuel_type}")
        if obj.engine: parts.append(f"Engine: {obj.engine}")
        if obj.model: parts.append(f"Model: {obj.model}")
        if obj.year: parts.append(f"Year: {obj.year}")
        return ", ".join(parts) if parts else "No criteria"
    get_criteria_summary.short_description = "Search Criteria"


@admin.register(SearchResult)
class SearchResultAdmin(admin.ModelAdmin):
    list_display = ['id', 'get_query_user', 'get_query_criteria', 'car', 'created_at']
    list_filter = ['created_at', 'search_query__user']
    search_fields = ['car__model__name', 'car__model__brand__name', 'search_query__user__username']
    readonly_fields = ['created_at']
    autocomplete_fields = ['car', 'search_query']
    date_hierarchy = 'created_at'

    def get_query_user(self, obj):
        return obj.search_query.user.username if obj.search_query.user else "Anonymous"
    get_query_user.short_description = "User"
    get_query_user.admin_order_field = 'search_query__user'

    def get_query_criteria(self, obj):
        return str(obj.search_query)[:60] + "..." if len(str(obj.search_query)) > 60 else str(obj.search_query)
    get_query_criteria.short_description = "Query"