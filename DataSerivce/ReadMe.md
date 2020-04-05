## Requirements
Python 3.5.2+

## Data Requiremnets
To download the data sets run the ```dataset_updater.py``` file in the scheduled_tasks folder

Don't forget to initialize the cron task on your production server. To do this run 

`sudo apt-get install postfix`
`crontab -e`

Don't forget to initialize the cron task on your production server. To do this either run the python file job_registrar.py or run:

`sudo apt-get install postfix`
`crontab -e`

and add the lines:

* */17 * * * /usr/bin/python3 ~/COMP3004/DataService/scheduled_tasks/dataset_updater.py >> data_updater_output.txt
* */1 * * * /usr/bin/python3 ~/COMP3004/DataService/scheduled_tasks/dataset_updater.py >> data_updater_output.txt

## Usage
nohup python scheduled_tasks/dataset_updater.py > nohup.out 2>&1 &
echo $! > save_pid.txt

To get the last file in a directory:
ls stock_data -Art | tail -n 1