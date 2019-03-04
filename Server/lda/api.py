from smtplib import SMTP_SSL as SMTP       # this invokes the secure SMTP protocol (port 465, uses SSL)
from email.mime.text import MIMEText
from os import path
from django.core.mail import send_mail
from math import sin, cos, sqrt, atan2, radians
from bigchaindb_driver import BigchainDB
from bigchaindb_driver.crypto import generate_keypair
from rest_framework import viewsets, permissions
from .models import Photo, Mobile_OTP, Admin, CustomUser, Authority
from .serializers import PhotoSerializer, FileListSerializer, MobileOTPSerializer
from twilio.rest import Client
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework import status
import json
import os
import hashlib
import time

import random

class CanalQuery(APIView):
    def post(self, request, *args, **kwargs):
        try:
            rq = request.POST.copy()
            datum = rq
            rid=userSave(request.data)
            return Response(
                data={
                    'id': rid
                },
                status=status.HTTP_201_CREATED,
            )
        except KeyError:
            return Response(
                data={},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR,
            )
class CanalRequest(APIView):
    def get(self, request, *args, **kwargs):
        try:
            rq = request.GET.copy()
            authority = request.GET['authority']
            datum = giveCustomQueries(authority)
            return Response(
                data=datum,
                status=status.HTTP_200_OK,
            )
        except KeyError:
            return Response(
                data={},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR,
            )
class PhoneRequest(APIView):
    def get(self, request, *args, **kwargs):
        try:
            rq = request.GET.copy()
            phone_number = request.GET['phone_number']
            datum = giveCustomQueries(phone_number)
            return Response(
                data=datum,
                status=status.HTTP_200_OK,
            )
        except KeyError:
            return Response(
                data={},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR,
            )
class ChangeState(APIView):
    def post(self, request, *args, **kwargs):
        try:
            rid = request.data["id"]
            status1 = request.data["status"]
            assigned_to = request.data.get("assigned_to", "NO_USER")
            admin_name = request.data.get("username", "")
            admin_pass = hashlib.sha256(request.data.get('password', "").encode('utf-8')).hexdigest()
            datum = UpdateState(rid, status1, assigned_to, admin_name, admin_pass)
            return Response(
                data={
                    'id': int(datum)
                },
                status=status.HTTP_200_OK,
            )
        except KeyError:
            return Response(
                data={},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR,
            )

class PhotoViewSet(viewsets.ModelViewSet):
    queryset = Photo.objects.all()
    permission_classes = [permissions.AllowAny, ]
    serializer_class = PhotoSerializer
    def create(self, request, *args, **kwargs):
        try:
            file = request.data['file']
        except KeyError:
            raise ParseError('Request has no resource file attached')
        query = Photo.objects.create(image=file, canalquery = request.data['id'])
        return Response(
            data={
                'Data': 'image was uploaded'
            },
            status=status.HTTP_201_CREATED,
        )

class MobileViewSet(viewsets.ModelViewSet):
    queryset = Mobile_OTP.objects.all()
    serializer_class = MobileOTPSerializer
    def create(self,request, *args, **kwargs):
        phone_number = request.data['phone']
        key = random.randint(999,9999)
        print(key)
        # send_otp(phone_number,key)
        account_sid = 'ACb3906fa619bb5300feaf663ef1c5d9a8'
        auth_token = '082d307c6eab31ea59ee7676ee67dfd3'
        client = Client(account_sid, auth_token)

        message = client.messages \
                    .create(
                        body=key,
                        from_='+19282564435',
                        to=phone_number
                    )
        new_object = Mobile_OTP.objects.create(phone_number = phone_number, otp=key)
        return Response(
            data={
                'Data': 'sent'
            },
            status=status.HTTP_201_CREATED,
        )
class VerifyViewSet(APIView):
    queryset = Mobile_OTP.objects.all()
    serializer_class = MobileOTPSerializer
    def post(self, request, format=None):
        phone_number = request.data.get('phone')
        otp = request.data.get('otp')
        newest_otp = Mobile_OTP.objects.filter(phone_number = phone_number).latest('created_at')
        if newest_otp.otp == otp:
            return Response(
            data={
                'Data': 'matched'
            },
            status=status.HTTP_200_OK,
        )
        else:
            return Response(
            data={
                'Data': 'not matched'
            },
            status=status.HTTP_200_OK,
        )

class AuthenticateAdmin(APIView):
    def post(self, request, format=None):
        admin_name = request.data.get('username')
        admin_password = hashlib.sha256(request.data.get('password', "").encode('utf-8')).hexdigest()
        admin = Admin.objects.filter(admin_name = admin_name, admin_password = admin_password).first()
        if admin:
            return Response(
            data={
                'authority': admin.admin_authority,
                'is_reviewer': admin.is_reviewer
            },
            status=status.HTTP_200_OK,
        )
        else:
            return Response(
            data={
                'Data': 'Not Authorised'
            },
            status=status.HTTP_403_FORBIDDEN,
        )

class AllRequests(APIView):
    def get(self, request, format=None):
        try:
            r = All()
            return Response(
                data=r,
                status=status.HTTP_200_OK,
            )
        except KeyError:
            return Response(
                data={},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR,
            )

class OneRequest(APIView):
    def get(self, request, format=None):
        try:
            r = One(request.GET["id"])
            return Response(
                data=r,
                status=status.HTTP_200_OK,
            )
        except KeyError:
            return Response(
                data={},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR,
            )


total_req = 0
bdb_root_url = 'http://localhost:9984'
bdb = BigchainDB(bdb_root_url)

#Check valid Status:
def check_valid_status(data):
    valid_list = ["Duplicate", "Spam", "Resolved", "Pending", "Assigned"]
    if data not in valid_list:
        raise ValidationError(
            'Invalid Status. Valid choices are'+ valid_list
        )

class Request:
    def __init__(self, data, rid):
        self.phoneNo = data.get("phone_number", "7302202200")
        self.id = rid
        self.assignedTo = data.get("assigned_to", "NO_USER")
        self.nMedia = data.get("n_media", 0)
        self.longitude = data.get("longitude", 0.0)
        self.latitude = data.get("latitude", 0.0)
        self.timestamp = int(time.time())
        self.waterBodyType = data.get("water_body_type", "Canal")
        self.status = data.get("status", "Pending")
        self.authority = getAuthority(self.longitude, self.latitude, self.waterBodyType)

def userSave(data):
    global total_req
    rid = total_req
    total_req += 1
    r = Request(data, "req_id_" + str(rid))
    user = CustomUser.objects.filter(user_phone_number = data['phone_number']).first()
    SaveText(rid, data.get("text", ""))
    if not user:
        userkey = generate_keypair()
        user = CustomUser.objects.create(user_phone_number=data.get("phone_number", "7302202200"), user_pubkey = userkey.public_key, user_privkey = userkey.private_key)
    asset = {
        'data': {
            'request': {
                'phone_number': r.phoneNo,
                'id': r.id,
                'assigned_to': r.assignedTo,
                'n_media': r.nMedia,
                'longitude': r.longitude,
                'latitude': r.latitude,
                'timestamp': r.timestamp,
                'water_body_type': r.waterBodyType,
                'status': r.status,
                'authority': r.authority
            },
        },
    }
    if CheckDuplicate(asset):
        raise KeyError('Duplicate')

    prepared_creation_tx = bdb.transactions.prepare(
        operation='CREATE',
        signers=user.user_pubkey,
        asset=asset
    )   

    fulfilled_creation_tx = bdb.transactions.fulfill(
        prepared_creation_tx,
        private_keys=user.user_privkey
    )

    sent_creation_tx = bdb.transactions.send_commit(fulfilled_creation_tx)

    if sent_creation_tx != fulfilled_creation_tx:
        raise KeyError('Not fulfilled')

    return rid

def giveCustomQueries(temp):
    r = search(str(temp))
    output_dict = []
    unique_reqid = []
    for x in reversed(r):
        if x["data"]["request"]["id"] not in unique_reqid:
            unique_reqid.append(x["id"])
            x["data"]["request"]["id"] = int(x["data"]["request"]["id"][7:])
            x["data"]["request"]["media"] = []
            io = Photo.objects.filter(canalquery = x["data"]["request"]["id"])
            for rr in io:
                x["data"]["request"]["media"].append(rr.id)
            if path.exists("media/text/req_id_" + str(x["data"]["request"]["id"])):
                f = open("media/text/req_id_" + str(x["data"]["request"]["id"]), "r")
                x["data"]["request"]["text"] = f.read()
            else:
                x["data"]["request"]["text"] = ""
            output_dict.append(x["data"]["request"])
    return output_dict

def search(keyword):
    print(keyword)
    print(bdb)
    print("keyword-=-=-=")
    return bdb.assets.get(search=keyword)

def getAuthority(longitude, latitude, water_body_type):
    R = 6373.0

    data = Authority.objects.filter(water_body_type = water_body_type)
    authority = "sih_central"
    mindata = 10000000000000000.0
    for x in data:
        lat1 = radians(float(x.latitude))
        lon1 = radians(float(x.longitude))
        lat2 = radians(float(latitude))
        lon2 = radians(float(longitude))

        dlon = lon2 - lon1
        dlat = lat2 - lat1

        a = sin(dlat / 2)**2 + cos(lat1) * cos(lat2) * sin(dlon / 2)**2
        c = 2 * atan2(sqrt(a), sqrt(1 - a))

        distance = R * c

        if distance < mindata:
            mindata = distance
            authority = x.authority_name
            pass

    return authority

def UpdateState(rid, status, assigned_to, admin_name, admin_pass):
    admin = Admin.objects.filter(admin_name = admin_name, admin_password = admin_pass).first()
    if not admin:
        return
    if admin.is_reviewer and (status != "Duplicate" or status != "Spam"):
        raise KeyError("Error Reviewer")
    asset = search("req_id_" + str(rid))[0]
    asset["data"]["request"]["status"] = status
    asset["data"]["request"]["assigned_to"] = assigned_to
    prepared_creation_tx = bdb.transactions.prepare(
        operation='CREATE',
        signers=admin.admin_pubkey,
        asset=asset
    )   
    fulfilled_creation_tx = bdb.transactions.fulfill(
        prepared_creation_tx,
        private_keys=admin.admin_privkey
    )

    sent_creation_tx = bdb.transactions.send_commit(fulfilled_creation_tx)

    if sent_creation_tx != fulfilled_creation_tx:
        raise KeyError('Not fulfilled')

    if status == "Assigned":
        sendmail()
        pass

    if status == "Resolved":
        phone_number = asset["data"]["request"]['phone_number']
        account_sid = 'ACb3906fa619bb5300feaf663ef1c5d9a8'
        auth_token = '082d307c6eab31ea59ee7676ee67dfd3'
        client = Client(account_sid, auth_token)
        message = client.messages \
                    .create(
                        body="Your query ",
                        from_='+19282564435',
                        to=phone_number
                    )
    return rid

def CheckDuplicate(asset):
    R = 6373.0
    r = search(asset["data"]["request"]["water_body_type"])
    
    mindata = 10000000000000000.0
    u = None
    for x in r:
        lat1 = radians(float(x["data"]["request"]["latitude"]))
        lon1 = radians(float(x["data"]["request"]["longitude"]))
        lat2 = radians(float(asset["data"]["request"]["latitude"]))
        lon2 = radians(float(asset["data"]["request"]["longitude"]))

        dlon = lon2 - lon1
        dlat = lat2 - lat1

        a = sin(dlat / 2)**2 + cos(lat1) * cos(lat2) * sin(dlon / 2)**2
        c = 2 * atan2(sqrt(a), sqrt(1 - a))
        distance = R * c
        if distance < 2 and abs(x["data"]["request"]["timestamp"] - asset["data"]["request"]["timestamp"]) < (2592000):
            return True
    return False

def SaveText(rid, text):
    cwd = os.getcwd()
    filename = "req_id_" + str(rid)
    f = open("media/text/" + filename, "w")
    f.write(text)
    f.close()


def All():
    r = search("central")
    output_dict = []
    unique_reqid = []
    for x in reversed(r):
        if x["data"]["request"]["id"] not in unique_reqid:
            unique_reqid.append(x["id"])
            output_dict.append({
                "longitude": x["data"]["request"]["longitude"],
                "latitude": x["data"]["request"]["latitude"],
                "status": x["data"]["request"]["status"],
                })
    return output_dict

def One(rid):
    r = search("req_id_" + str(rid))
    output_dict = []
    unique_reqid = []
    for x in reversed(r):
        print(x, "LLLLLLLLLLLLLLLLLLLLLLLL")
        x["data"]["request"]["id"] = int(x["data"]["request"]["id"][7:])
        x["data"]["request"]["media"] = []
        io = Photo.objects.filter(canalquery = x["data"]["request"]["id"])
        for rr in io:
            x["data"]["request"]["media"].append(rr.id)
        print(x["data"]["request"]["id"], "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL")
        if path.exists("media/text/req_id_" + str(x["data"]["request"]["id"])):
            f = open("media/text/req_id_" + str(x["data"]["request"]["id"]), "r")
            x["data"]["request"]["text"] = f.read()
        else:
            x["data"]["request"]["text"] = ""
        x["data"]["request"]["longitude"] = str(x["data"]["request"]["longitude"])
        x["data"]["request"]["latitude"] = str(x["data"]["request"]["latitude"])
        print(x["data"]["request"])
        return x["data"]["request"]
    raise KeyError("Not Here")


def sendmail():
    SMTPserver = 'imap.gmail.com'
    sender =     'demo.sih.2019@gmail.com'
    destination = ['gkishan@cs.iitr.ac.in','ssingh@cs.iitr.ac.in' , 'sbindal@cs.iitr.ac.in' , 'psethia@cs.iitr.ac.in']

    USERNAME = "demo.sih.2019@gmail.com"
    PASSWORD = "sihdemo2019"

    # typical values for text_subtype are plain, html, xml
    text_subtype = 'plain'


    content="""\
    You are requested to please look into the matter and resolve the issue as soon as possible.
    """

    subject="Assignment of issue" # message goes here

    # import sys
    # import os
    # import re

    # from smtplib import SMTP                  # use this for standard SMTP protocol   (port 25, no encryption)

    # old version
    # from email.MIMEText import MIMEText
    print("imports done")
    # try:
    msg = MIMEText(content, text_subtype)
    msg['Subject']= subject
    msg['From']   = sender # some SMTP servers will do this automatically, not all
    print("msg created")
    conn = SMTP(SMTPserver , port=465)
    # conn = SMTP("localhost")
    print("conn established")
    # conn.set_debuglevelSMTPserverel(False)
    conn.login(USERNAME, PASSWORD)
    print("logged in")
    try:
        conn.sendmail(sender, destination, msg.as_string())
        print("mail sent")
    finally:
        conn.quit()
        print("logged out")


    # except:
    #     sys.exit( "mail failed; %s" % "CUSTOM_ERROR" )