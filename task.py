from celery import Celery
from flask import Flask
import requests

def make_celery(app):
    celery = Celery(
        app.import_name,
        broker=app.config['CELERY_BROKER_URL']
    )
    celery.conf.update(app.config)

    class ContextTask(celery.Task):
        def __call__(self, *args, **kwargs):
            with app.app_context():
                return self.run(*args, **kwargs)
    celery.Task = ContextTask
    return celery

app = Flask(__name__)
app.config.update(
    CELERY_BROKER_URL='amqp://guest@localhost//'
)
celery = make_celery(app)

@celery.task()
def notify(chatroom_id, chatroom_name, name, message, token):
    api_key = 'AAAAbsb1ts4:APA91bGBL7k4Bn1_sD9gmDgkvS1i7j7Nj1M9DWCgfZTxNoLG8KBhPsxltUtiCUirzEYTr-4W4SpR6l0bnqfLsO6Ab4LivGmIaoEJJKCxdH1wHW_catp3ZnhWhEsP3u_PO-_gUysRIOsK'
    url = 'https://fcm.googleapis.com/fcm/send'

    headers = {
        'Authorization': 'key=' + api_key,
        'Content-Type': 'application/json'
    }

    device_token = token
    payload = {
        'to' : device_token,
        'notification' : {
            "title": chatroom_name,
            "tag": chatroom_id,
            "body": name+":"+message
        }
    }

    r = requests.post(url, headers = headers, json = payload)
    if r.status_code == 200:
        print('OK')