from django.core.management import call_command
from django.core.management.base import BaseCommand
from django.core.management.commands.runserver import BaseRunserverCommand


class Command(BaseRunserverCommand):
    ''' Runs the built-in Django runserver with initial schema and fixtures
    Note: cannot use auto-reloading because that casues resetdb to be called
    twice.
    '''
    # remove this option from the --help for this command
    help = 'Starts a lightweight web server testing.'

    def add_arguments(self, parser):
        # set up training configuration.
        parser.add_argument('--gpu', default='0', type=str)
        parser.add_argument('--resume', default=r'ghostvlad/pretrained/weights.h5', type=str)
        parser.add_argument('--data_path', default='4persons', type=str)
        # set up network configuration.
        parser.add_argument('--net', default='resnet34s', choices=['resnet34s', 'resnet34l'], type=str)
        parser.add_argument('--ghost_cluster', default=2, type=int)
        parser.add_argument('--vlad_cluster', default=8, type=int)
        parser.add_argument('--bottleneck_dim', default=512, type=int)
        parser.add_argument('--aggregation_mode', default='gvlad', choices=['avg', 'vlad', 'gvlad'], type=str)
        # set up learning rate, training loss and optimizer.
        parser.add_argument('--loss', default='softmax', choices=['softmax', 'amsoftmax'], type=str)
        parser.add_argument('--test_type', default='normal', choices=['normal', 'hard', 'extend'], type=str)

 
        


    def handle(self, addrport='', *args, **options):
        print("")
