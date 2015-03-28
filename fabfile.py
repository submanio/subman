from fabric.api import run, cd


def update():
    with cd('~/subman'):
        run('git pull')
        run('docker-compose pull')
        run('docker-compose up -d web parser nginx')
