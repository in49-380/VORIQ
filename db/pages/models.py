from django.db import models
from django.contrib.auth.models import User

# Brand model
class Brand(models.Model):
    ''' 
    Model for storing car brands (for example, Toyota, BMW, Mercedes). 
    Unique constraint on the name is used to prevent duplication.
    '''
    # id is automatically created by Django
    name = models.CharField(max_length=100, unique=True, verbose_name="Brand Name")
    
    def __str__(self):
        return self.name
    
    class Meta:
        verbose_name = "Brand"
        verbose_name_plural = "Brands"
        indexes = [models.Index(fields=['name'])]
      
# Fuel Type model
class FuelType(models.Model):
    """
    Model for storing fuel types (gasoline, diesel, electricity, hybrid). 
    It is used as a reference for engines and search queries.    
    ID is automatically created by Django.
    """
    name = models.CharField(max_length=50, unique=True, verbose_name="Fuel Type Name")
    
    def __str__(self):
        return self.name
    
    class Meta:
        verbose_name = "Fuel Type"
        verbose_name_plural = "Fuel Types"
        # Index for fast searching by fuel type.
        indexes = [models.Index(fields=['name'])]
      
# Engine model
class Engine(models.Model):
    """
    Connected to the fuel type through a ForeignKey with SET_NULL to preserve engine data even 
    when the fuel type is deleted.
    ID is automatically created by Django
    """
    type = models.CharField(max_length=50, verbose_name="Engine Type")
    # SET_NULL is used to preserve engine records when the fuel type is deleted.
    fuel_type = models.ForeignKey(FuelType, on_delete=models.SET_NULL, null=True, verbose_name="Fuel Type")
    
    def __str__(self):
        return f"{self.type} ({self.fuel_type})"
    
    class Meta:
        verbose_name = "Engine"
        verbose_name_plural = "Engines"

# Car Model (model) 
class CarModel(models.Model):
    """
    Model for storing car models (e.g., Camry, X5, C-Class). It is connected to the brand through 
    a ForeignKey with CASCADE - when the brand is deleted, all its models are deleted. 
    unique_together prevents duplication of models within the same brand.
    ID is automatically created by Django
    """
    name = models.CharField(max_length=100, verbose_name="Model Name")
    # CASCADE is used because the model cannot exist without the brand.
    brand = models.ForeignKey(Brand, on_delete=models.CASCADE, verbose_name="Brand")
    
    def __str__(self):
        return f"{self.brand.name} {self.name}"
    
    class Meta:
        verbose_name = "Car Model"
        verbose_name_plural = "Car Models"
        # The uniqueness of the combination of model name + brand.
        unique_together = ['name', 'brand']

# Car Year Reference 
class Year(models.Model):

    year = models.IntegerField(verbose_name="Production Year")
    
    def __str__(self):
        return str(self.year)
    
    class Meta:
        verbose_name = "Production Year"
        verbose_name_plural = "Production Years"
        # Sorting by year in descending order (newer years first)
        ordering = ['-year']

# Actual Car model 
class Car(models.Model):
    """
    Table that combines all characteristics.
    ID is automatically created by Django
    """

    # SET_NULL is used to preserve car records when related data is deleted.
    model = models.ForeignKey(CarModel, on_delete=models.SET_NULL, null=True, verbose_name="Model")
    engine = models.ForeignKey(Engine, on_delete=models.SET_NULL, null=True, verbose_name="Engine")
    year = models.ForeignKey(Year, on_delete=models.SET_NULL, null=True, verbose_name="Production Year")
    
    def __str__(self):
        return f"{self.model} ({self.year}) - {self.engine}"
    
    class Meta:
        verbose_name = "Car"
        verbose_name_plural = "Cars"

# Search Query model
class SearchQuery(models.Model):
    """
    Model for storing user search queries. All search criteria are optional (null=True, blank=True). 
    Relationship with the user through CASCADE - when the user is deleted, their queries are deleted.
    ID is automatically created by Django
    """
    # CASCADE is used for deleting requests when a user is deleted.
    user = models.ForeignKey(User, on_delete=models.CASCADE, verbose_name="User", null=True, blank=True)
    
    
    # Fields for search criteria (all optional)
    # SET_NULL is used to preserve queries when reference data is deleted
    brand = models.ForeignKey(Brand, on_delete=models.SET_NULL, null=True, blank=True, verbose_name="Brand")
    fuel_type = models.ForeignKey(FuelType, on_delete=models.SET_NULL, null=True, blank=True, verbose_name="Fuel Type")
    engine = models.ForeignKey(Engine, on_delete=models.SET_NULL, null=True, blank=True, verbose_name="Engine")
    model = models.ForeignKey(CarModel, on_delete=models.SET_NULL, null=True, blank=True, verbose_name="Model")
    year = models.ForeignKey(Year, on_delete=models.SET_NULL, null=True, blank=True, verbose_name="Year")
    
    created_at = models.DateTimeField(auto_now_add=True, verbose_name="Created At")
    query_name = models.CharField(max_length=200, blank=True, verbose_name="Query Name")
    
    def __str__(self):

        # Creates a readable representation of the search query.
        criteria = []
        if self.brand:
            criteria.append(f"Brand: {self.brand}")
        if self.fuel_type:
            criteria.append(f"Fuel: {self.fuel_type}")
        if self.engine:
            criteria.append(f"Engine: {self.engine}")
        if self.model:
            criteria.append(f"Model: {self.model}")
        if self.year:
            criteria.append(f"Year: {self.year}")
        
        criteria_str = ", ".join(criteria) if criteria else "No criteria"
        return f"Search Query #{self.id}: {criteria_str}"
    
    class Meta:
        verbose_name = "Search Query"
        verbose_name_plural = "Search Queries"
        # Sorting by creation date (newest requests first)
        ordering = ['-created_at']

# Search Result model
class SearchResult(models.Model):
    """
    Model for storing search query results. Connects the search query with found cars. 
    unique_together prevents duplication of a single car in the results of one query.    
    ID is automatically created by Django
    """

    # CASCADE is used because the results are meaningless without the query.
    search_query = models.ForeignKey(SearchQuery, on_delete=models.CASCADE, verbose_name="Search Query", related_name="results")
    
    # SET_NULL is used to preserve records of results even when the car is deleted.
    car = models.ForeignKey(Car, on_delete=models.SET_NULL, null=True, verbose_name="Car")
    
    # Date and time of record creation.
    created_at = models.DateTimeField(auto_now_add=True, verbose_name="Found At")

    def __str__(self):
        return f"Result for Query #{self.search_query.id}: {self.car}"
    
    class Meta:
        verbose_name = "Search Result"
        verbose_name_plural = "Search Results"
        ordering = ['-created_at']
        # Uniqueness: one car can appear only once in the results of a single query
        unique_together = ['search_query', 'car']