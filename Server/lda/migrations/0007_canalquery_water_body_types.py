# Generated by Django 2.1.7 on 2019-03-02 14:10

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('lda', '0006_auto_20190302_1333'),
    ]

    operations = [
        migrations.AddField(
            model_name='canalquery',
            name='water_body_types',
            field=models.CharField(default='Canal', max_length=13),
        ),
    ]
