from django.conf.urls import include, url
from rest_framework import routers

from .api import PhotoViewSet, MobileViewSet, VerifyViewSet, AuthenticateAdmin, CanalRequest, PhoneRequest, ChangeState, AllRequests, CanalQuery, OneRequest

router = routers.DefaultRouter()
router.register('photo', PhotoViewSet, 'photo')
router.register('otp_request', MobileViewSet, 'otp_request')
# router.register('verify', VerifyViewSet)

urlpatterns = [
    url("^", include(router.urls)),
    url("^verify/",VerifyViewSet.as_view()),
    url("^authenticate/", AuthenticateAdmin.as_view()),
    url("^request/create/", CanalQuery.as_view()),
    url("^request/query/", CanalRequest.as_view()),
    url("^request/phonequery/", PhoneRequest.as_view()),
    url("^request/changestate/", ChangeState.as_view()),
    url("^request/all/", AllRequests.as_view()),
    url("^query/", OneRequest.as_view())
]