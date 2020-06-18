import sys
sys.path.insert(1,"../../")
import h2o
from h2o.estimators import H2OGradientBoostingEstimator
from tests import pyunit_utils

def kolmogorov_smirnov():
    # Train a model
    airlines = h2o.import_file(path=pyunit_utils.locate("smalldata/testng/airlines_train.csv"))
    model = H2OGradientBoostingEstimator(ntrees=1)
    model.train(x=["Origin", "Distance"], y="IsDepDelayed", training_frame=airlines)

    #Fetch and print gains-lift table
    gainslift = model.gains_lift()
    print(gainslift)

    # Fetch original kolmogorov-smirnov metric
    ks = model.kolmogorov_smirnov()
    #Verify the metric by using in-place calculation
    ks_verification = ks_metric(model, airlines)
    print(ks_verification)

    assert round(ks, 5) == round(ks_verification, 5)


def ks_metric(model, data):
    # Author: Megan Kurka
    # get model predictions
    y = model.params.get('response_column').get('actual').get('column_name')
    preds = model.predict(data)["YES"].cbind(data[y])
    preds.col_names = ["prediction", "actual"]
    # bin records into prediction deciles
    import numpy as np
    breaks = preds["prediction"].quantile(prob = list(np.arange(0.1, 1.0, 0.01)))
    breaks = list(breaks.as_data_frame()["predictionQuantiles"])
    preds["bin"] = "bin0"
    preds["bin"] = preds["bin"].asfactor()
    for i in range(len(breaks)):
        preds["bin"] = (preds["prediction"] > breaks[i]).ifelse("bin" + str(i), preds["bin"])
    # calculate ks
    ks_stats = preds.group_by("bin").min("prediction").sum("actual").count().get_frame()
    ks_stats = ks_stats.sort("min_prediction", ascending = [False])
    ks_stats["bin"]
    cum_event = (ks_stats["sum_actual"]/ks_stats["sum_actual"].sum()).cumsum()
    cum_non_event = ((ks_stats["nrow"] - ks_stats["sum_actual"])/(ks_stats["nrow"] - ks_stats["sum_actual"]).sum()).cumsum()
    return (cum_event - cum_non_event).max()

if __name__ == "__main__":
    pyunit_utils.standalone_test(kolmogorov_smirnov)
else:
    kolmogorov_smirnov()
