# Generated by Django 2.1.7 on 2019-03-01 19:22

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('lda', '0003_auto_20190301_1838'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='photo',
            name='canalquery',
        ),
    ]