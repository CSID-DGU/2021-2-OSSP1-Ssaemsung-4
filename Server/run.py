from django.core.wsgi import get_wsgi_application
from django.core.management import call_command
import os

os.environ.setdefault("DJANGO_SETTINGS_MODULE", "restfulapiserver.settings")


application = get_wsgi_application()
call_command("runserver", "127.0.0.1:8000")
#call_command("startapp", "summarization")
# call_command('migrate')
