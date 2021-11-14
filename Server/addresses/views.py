from django.shortcuts import render
from django.http import HttpResponse, JsonResponse
from django.views.decorators.csrf import csrf_exempt
from rest_framework.parsers import JSONParser
from .models import Addresses
from .serializers import AddressesSerializer
# Create your views here.

# 어떤 데이터를 받고 실제로 구현하는 logic을 만드는 부분


# http 통신이라 request, respond가 있어야함
@csrf_exempt
def address_list(request):
    if request.method == 'GET':
        query_set = Addresses.objects.all()
        serializer = AddressesSerializer(query_set, many=True)
        return JsonResponse(serializer.data, safe=False)

    elif request.method == 'POST':
        data = JSONParser().parse(request)  # 올라온 데이터랑
        serializer = AddressesSerializer(data=data)  # 우리 모델 데이터 형식이랑 클라이언트에서 올린 형식이랑 같으면
        if serializer.is_valid():
            serializer.save()
            return JsonResponse(serializer.data, status=201)  # 데이터가 잘 만들어졌다
        return JsonResponse(serializer.errors, status=400)  # 실패