package hex.genmodel.algos.gam;

import hex.genmodel.ModelMojoReader;

import java.io.IOException;

public class GamMojoReader extends ModelMojoReader<GamMojoModelBase> {

  @Override
  public String getModelName() {
    return "Generalized Additive Model";
  }

  @Override
  protected void readModelData() throws IOException {
    _model._useAllFactorLevels = readkv("use_all_factor_levels", false);
    _model._cats = readkv("cats", -1);
    _model._catNAFills = readkv("catNAFills", new int[0]);
    _model._numNAFills = readkv("numNAFills", new double[0]);
    _model._meanImputation = readkv("mean_imputation", false);
    _model._beta = readkv("beta");
    _model._family = readkv("family");
    
    if (_model instanceof GamMojoModel) {
      GamMojoModel m = (GamMojoModel) _model;
      m._link = readkv("link");
      m._tweedieLinkPower = readkv("tweedie_link_power", 0.0);
    }
    
    _model.init();
  }

  @Override
  protected GamMojoModelBase makeModel(String[] columns, String[][] domains, String responseColumn) {
    return new GamMojoModel(columns, domains, responseColumn);
  }

  @Override
  public String mojoVersion() {
    return "1.00";
  }
}
