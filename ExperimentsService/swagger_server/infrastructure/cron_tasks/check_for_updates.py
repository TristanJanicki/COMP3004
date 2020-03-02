import concurrent.futures
from swagger_server import optionsLearner
from swagger_server.infrastructure.db.mysql import mysql
from swagger_server.database_models import ThresholdExperiment, CorrelationExperiment
sqlManager = mysql.SqlManager()


# with concurrent.futures.ThreadPoolExecutor() as executor:

#     for thExp in sqlManager.session.query(ThresholdExperiment).filter_by(status="update_requested"):
#         optionsLearner.Thres5

#     for corrExp in sqlManager.session.query(CorrelationExperiment).filter_by(status="update_requested"):
    
    

#     future = executor.submit(get_rsi_threshold_move_distribution, tickers, "ALL",  1 + i, j, False)
#     futures.append(future)
#     moves_distribution = future.result()[3]