# Run this file to register the experiment update checker cron job

import options
import os
from crontab import CronTab
import time
log_file = "~/options_updater_cron_log.txt"
f = open(log_file, "w+")
f.write("Experiments Service Cron Log For Options Data Updater " + time.asctime())

cron = CronTab(user=True, log=log_file)
job = cron.new(command='/usr/bin/python3 check_for_updates.py >> ' + log_file)
job.minute.every(1)
cron.write()