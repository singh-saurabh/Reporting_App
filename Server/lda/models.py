from django.contrib.auth.models import User
from django.db import models

# Create your models here.
def productFile(instance, filename):
    return '/'.join( ['products_changed', filename] )

class Photo(models.Model):
    canalquery = models.IntegerField(blank=False, default=-1)
    image = models.ImageField(
        upload_to='media',
        max_length=254, blank=True, null=True
    )
    def __str__(self):
        return "this is an image"

class Mobile_OTP(models.Model):
    phone_number = models.CharField(max_length=255, blank=True, default='8295729539')
    otp = models.CharField(max_length=5, blank=True, default = '1000')
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return "this function is used to generate OTP"

class Admin(models.Model):
    admin_name = models.CharField(max_length=255, blank=True, default='admin_new')
    admin_password = models.CharField(max_length=255, blank=False, default = 'pass')
    admin_pubkey = models.CharField(max_length=255, blank=True)
    admin_privkey = models.CharField(max_length=255, blank=True)
    admin_authority = models.CharField(max_length=255, blank=False, default = 'sih_central')
    is_reviewer = models.BooleanField(blank=False, default = False)

class CustomUser(models.Model):
    user_phone_number = models.CharField(max_length=255, blank=True, default='7302202200')
    user_pubkey = models.CharField(max_length=255, blank=False)
    user_privkey = models.CharField(max_length=255, blank=False)

class Authority(models.Model):
    latitude = models.CharField(max_length=255, blank=False, default='0.0')
    longitude = models.CharField(max_length=255, blank=False, default='0.0')
    water_body_type = models.CharField(max_length=255, blank=False, default='Canal')
    authority_name = models.CharField(max_length=255, blank=False, default='sih_central')
