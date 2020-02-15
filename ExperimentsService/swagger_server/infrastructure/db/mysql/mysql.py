import sqlalchemy as db
from sqlalchemy.orm import sessionmaker

class SqlManager:

    metadata = db.MetaData()
    connection = None
    engine = None
    db = db
    session = None

    def __init__(self):
        # super().__init__()
        self.endpoint = "quantr.cii6qa7deotz.us-east-1.rds.amazonaws.com"
        self.port = "3306"
        self.user = "admin"
        self.password = "5efPemPEwZrBfhvQ"
        self.database = "quantr"
        self.engine = db.create_engine(
            "mysql+pymysql://"+self.user+":"+self.password+"@"+self.endpoint+":"+self.port+"/"+self.database)
        self.connection = self.engine.connect()
        Session = sessionmaker(bind=self.engine)
        self.session = Session()

    def selectAll(self, model):
        query = db.select(model)
        queryResult = self.connection.execute(query)
        resultSet = queryResult.fetchall()
        return resultSet

    def insert(self, model):
        self.Session.add(model)