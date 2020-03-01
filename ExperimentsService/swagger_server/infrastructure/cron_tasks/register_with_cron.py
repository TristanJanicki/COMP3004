# Run this file to register the experiment update checker cron job

from crontab import CronTab

cron = CronTab(user='ubuntu')
job = cron.new(command='python check_for_updates.py')
job.minute.every(1)

cron.write()