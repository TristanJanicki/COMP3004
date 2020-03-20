import options
import os
from crontab import CronTab
import time
log_file = "./options_updater_cron_log.txt"
if not os._exists(log_file):
    f = open(log_file, "w")
    f.write("Cron Log For Options Data Updater " + time.asctime())

job = CronTab(tab="python3 ~/COMP34004/DataService/scheduled_tasks/options_updater.py", log=log_file)
job.