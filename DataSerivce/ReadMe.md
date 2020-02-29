## Requirements
Python 3.5.2+

## Data Requiremnets
To download the data sets run the ```dataset_updater.py``` file in the scheduled_tasks folder

Don't forget to initialize the cron task on your production server. To do this run 

`sudo apt-get install postfix`
`crontab -e`

Don't forget to initialize the cron task on your production server. To do this run 

`sudo apt-get install postfix`
`crontab -e`

and add the line:

* */17 * * * /usr/bin/python ~/COMP3004/DataService/scheduled_tasks/dataset_updater.py >> data_updater_output.txt
