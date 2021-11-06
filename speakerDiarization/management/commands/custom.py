from django.core.management.base import BaseCommand, CommandError
import argparse

class Command(BaseCommand):
    help = '타이틀를 입력하여 질문을 만듭니다.'


    def add_arguments(self, parser):
        # set up training configuration.
        parser.add_argument('--gpu', default='', type=str)
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

        args = parser.parse_args()


    def handle(self, *args, **options):
        print(" ")
        return args