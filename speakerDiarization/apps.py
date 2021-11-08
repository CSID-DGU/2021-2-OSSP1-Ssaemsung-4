from django.apps import AppConfig
from speakerDiarization.diarization.ghostvlad import model as spkModel

class SpeakerdiarizationConfig(AppConfig):

    default_auto_field = 'django.db.models.BigAutoField'
    name = 'speakerDiarization'

    params = {'dim': (257, None, 1),
              'nfft': 512,
              'spec_len': 250,
              'win_length': 400,
              'hop_length': 160,
              'n_classes': 5994,
              'sampling_rate': 16000,
              'normalize': True,
              }
    
    network_eval = spkModel.vggvox_resnet2d_icassp(input_dim=params['dim'],
                                                   num_class=params['n_classes'],
                                                   mode='eval')
