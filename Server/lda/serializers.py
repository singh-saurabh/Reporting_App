from rest_framework import serializers

from .models import Photo, Mobile_OTP

class FileListSerializer ( serializers.Serializer ) :
    image = serializers.ListField(
                       child=serializers.FileField( max_length=100000,
                                         allow_empty_file=False,
                                         use_url=False)
                                )
    def create(self, validated_data):
        query=CanalQuery.objects.latest('created_at')
        image=validated_data.pop('image')
        for img in image:
            photo=Photo.objects.create(image=img,canalquery = query,**validated_data)
        return photo

class PhotoSerializer(serializers.ModelSerializer):
    class Meta:
        model = Photo
        # read_only_fields = ("canalquery",)
        fields = ('image','canalquery',)

class MobileOTPSerializer(serializers.ModelSerializer) :
    class Meta:
        model = Mobile_OTP
        fields = ('phone_number','otp','created_at')
