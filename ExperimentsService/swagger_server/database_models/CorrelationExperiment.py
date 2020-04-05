from sqlalchemy import Column, Integer, String, Float, Boolean, DateTime
from sqlalchemy.ext.declarative import declarative_base
import uuid
Base = declarative_base()

class CorrelationExperiment(Base):
    __tablename__ = 'correlation_experiments'
    experiment_id = Column(String, primary_key=True)
    asset_1 = Column(String)
    asset_2 = Column(String)
    asset_1_deltas = Column(String)
    asset_2_deltas = Column(String)
    correlation = Column(Float)
    # status = Column(Enum(StatusEnum)) #TODO: figure out how to properly query with this column type and then enforce this enum restriction at the database level. Status can be: update_requested, up_to_date, current_updating
    status = Column(String)
    # asset_combo = Enum(Enum(AssetCombo)) #TODO: same as above
    asset_combo = Column(String) # should only be 'equity_currency','currency_currency'
    update_requested_at = Column(DateTime)
    last_updated_at = Column(DateTime)

    def __init__(self, experiment_id, asset_1, asset_2, correlation=0, status="update_requested", asset_combo="equit_equit", update_requested_at=None, last_updated_at=None, asset_1_deltas=[], asset_2_deltas=[]):
        self.experiment_id = experiment_id
        self.asset_1 = asset_1
        self.asset_2 = asset_2
        self.correlation = correlation
        self.status = status
        self.asset_combo = asset_combo
        self.asset_1_deltas = asset_1_deltas
        self.asset_2_deltas = asset_2_deltas
        self.update_requested_at = update_requested_at
        self.last_updated_at = last_updated_at

    def __repr__(self):
        return "<CorrelationExperiment(%s_%s    %f  %s  %s   %s)>" % (self.asset_1, self.asset_2, self.correlation, self.status, self.update_requested_at, self.last_updated_at)