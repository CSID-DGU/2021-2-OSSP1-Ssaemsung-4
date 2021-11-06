from django import forms


class AudioUploadForm(forms.Form):
    audio = forms.FileField()