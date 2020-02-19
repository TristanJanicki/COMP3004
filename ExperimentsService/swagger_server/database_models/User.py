from sqlalchemy import Column, Integer, String, Float, DateTime
from sqlalchemy.ext.declarative import declarative_base
Base = declarative_base()


class User(Base):
    __tablename__ = 'threshold_experiments'
    user_id = Column(String, primary_key=True)
    first_name = Column(String)
    last_name = Column(String)
    email = Column(String)
    nickname = Column(String)
    title = Column(String)
    country = Column(String)
    country_code = Column(String)
    phone_number = Column(String)
    experiments = Column(String)
    created_at = Column(DateTime)
    updated_at = Column(DateTime)
    deleted_at = Column(DateTime)

    def __init__(self, user_id, first_name, last_name, email, nickname, title, country,country_code, phone_number, experiments, created_at, updated_at, deleted_at):
        self.user_id = user_id
        self.first_name = first_name
        self.last_name = last_name
        self.email = email
        self.nickname = nickname
        self.title = title
        self.country = country
        self.country_code = country_code
        self.phone_number = phone_number
        self.experiments = experiments
        self.created_at= created_at
        self.updated_at = updated_at
        self.deleted_at = deleted_at
