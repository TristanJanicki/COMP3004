## Requirements
Python 3.5.2+

## Data Requiremnets
To download the data sets run the ```name TBD``` file in the scheduled_tasks folder

Don't forget to initialize the cron task on your production server. To do this run 

`sudo apt-get install postfix`
`crontab -e`

and add the line:

* */3 * * * /usr/bin/python ~/COMP3004/DataService/scheduled_tasks/dataset_updater.py >> dataset_updater_output.txt

output from these files 

TODO: Fill Out Usage
## Usage