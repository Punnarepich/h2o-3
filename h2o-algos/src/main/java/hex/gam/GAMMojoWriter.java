package hex.gam;


import hex.ModelMojoWriter;
import hex.glm.GLMModel;

import java.io.IOException;

public class GAMMojoWriter extends ModelMojoWriter<GAMModel, GAMModel.GAMParameters, GAMModel.GAMModelOutput> {
  @Override
  public String mojoVersion() {
    return "1.00";
  }

  @Override
  protected void writeModelData() throws IOException {
    writekv("use_all_factor_levels", model._parms._use_all_factor_levels);
    writekv("cats", model._output._dinfo._cats);
    writekv("cat_offsets", model._output._dinfo._catOffsets);
    writekv("nums", model._output._dinfo._nums);
    
    boolean imputeMeans = model._parms.missingValuesHandling().equals(GLMModel.GLMParameters.MissingValuesHandling.MeanImputation);
    writekv("mean_imputation", imputeMeans);
    if (imputeMeans) {
      writekv("num_means", model._output._dinfo.numNAFill());
      writekv("cat_modes", model._output._dinfo.catNAFill());
    }
    
    writekv("beta", model._output._model_beta);
    writekv("family", model._parms._family);
    writekv("link", model._parms._link);
    
    if (model._parms._family.equals(GLMModel.GLMParameters.Family.tweedie))
      writekv("tweedie_link_power", model._parms._tweedie_link_power);
    // add GAM specific parameters
    writekv("_num_knots", model._parms._num_knots);
    writekv("_gam_columns", model._parms._gam_columns);
    writekv("bs", model._parms._bs);
    // store variable importance information
  }
}
