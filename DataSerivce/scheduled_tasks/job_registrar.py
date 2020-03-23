import os
from crontab import CronTab
import time
log_file = "~/options_updater_cron_log.txt"
f = open(log_file, "a")
f.write("Data Service Cron Log For Options Data Updater " + time.asctime())

cron = CronTab(user=True)
job = cron.new(command="/usr/bin/python3 ~/COMP34004/DataService/scheduled_tasks/options_dataset_updater.py >> " + log_file)
job.minute.every(1) # run every n minutes
cron.write() # save it



job = cron.new(command="/usr/bin/python3 ~/COMP3004/DataService/scheduled_tasks/dataset_updater.py >> " + log_file)
job.hour.every(17) # run at 17:00hrs
job.hour.also.on(1) # also run at 03:00hrs
cron.write() # save it

for item in cron:
    print(item)