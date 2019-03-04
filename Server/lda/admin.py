from django.contrib import admin
from .models import Photo, Mobile_OTP, Admin, CustomUser, Authority

admin.site.register(Photo)
admin.site.register(Mobile_OTP)
admin.site.register(Admin)
admin.site.register(CustomUser)
admin.site.register(Authority)

# Register your models here.
