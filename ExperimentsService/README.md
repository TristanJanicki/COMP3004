## Requirements
Python 3.5.2+

## Data Requiremnets
To download the data sets run the ```name TBD``` file in the scheduled_tasks folder
To force an update of experiments run the ```name TBD``` file in the scheduled_tasks folder

Don't forget to initialize the cron task on your production server. To do this run 

`sudo apt-get install postfix`
`crontab -e`

and add the line:

* */3 * * * /usr/bin/python ~/COMP3004/ExperimentsSerivce/scheduled_tasks/experiment_updater.py >> experiment_updater_output.txt

output from these files 

## Usage
To run the server, please execute the following from the root directory:

```
pip3 install -r requirements.txt
python3 -m swagger_server
```

and open your browser to here:

```
http://localhost:8080/ui/
```

The Swagger definition lives here:

```
http://localhost:8080/swagger.json
```

To launch the integration tests, use tox:
```
sudo pip install tox
tox
```

## Running with Docker

To run the server on a Docker container, please execute the following from the root directory:

```bash
# building the image
docker build -t swagger_server .

# starting up a container
docker run -p 8080:8080 swagger_server
```