from sqlalchemy import Column, Integer, String, Float, DateTime, Text
from sqlalchemy.ext.declarative import declarative_base
Base = declarative_base()


class User(Base):
    __tablename__ = 'user_accounts'
    user_id = Column(String, primary_key=True)
    name = Column(String)
    email = Column(String)
    nickname = Column(String)
    title = Column(String)
    country = Column(String)
    country_code = Column(String)
    phone_number = Column(String)
    experiments = Column(Text)
    created_at = Column(DateTime)
    updated_at = Column(DateTime)
    deleted_at = Column(DateTime)

    def __init__(self, user_id, name, email, nickname, title, country,country_code, phone_number, experiments, created_at, updated_at, deleted_at):
        self.user_id = user_id
        self.name = name
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

    def __repr__(self):
        return "<User(user_id=%s, name=%s, email=%s, nickname=%s, title=%s, country=%s, country_code=%s, phone_number=%s, experiments=%s, created_at=%s, updated_at=%s, deleted_at=%s)>" % (self.user_id, self.name, self.email, self.nickname, self.title, self.country, self.country_code, self.phone_number, str(self.experiments), self.created_at, self.updated_at, self.deleted_at)
