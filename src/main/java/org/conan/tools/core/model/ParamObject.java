package org.conan.tools.core.model;

import java.util.ArrayList;
import java.util.List;

import org.conan.tools.core.util.PropertyBean;
import org.conan.tools.core.util.SqlFinderBean;
import org.conan.tools.core.util.TableBean;
import org.conan.tools.core.xmlloader.DaoToolType;
import org.conan.tools.core.xmlloader.FinderType;
import org.conan.tools.core.xmlloader.ModelType;
import org.conan.tools.core.xmlloader.ModuleType;
import org.conan.tools.core.xmlloader.PropType;
import org.conan.tools.util.match.JavaSQLMatch;
import org.conan.tools.util.match.StringMatch;

/**
 * 
 * @author conan
 */
public class ParamObject {

    private FilePO filePO;
    private List<PackagePO> packageList = new ArrayList<PackagePO>(50);
    private List<ClazzPO> clazzList = new ArrayList<ClazzPO>(50);
    private List<ModelPO> modelList = new ArrayList<ModelPO>(50);
    private List<FormPO> formList = new ArrayList<FormPO>(50);
    private List<ModuleModelPO> mmList = new ArrayList<ModuleModelPO>(20);
    private List<DaoPO> daoList = new ArrayList<DaoPO>(50);
    private List<IbatisPO> ibatisList = new ArrayList<IbatisPO>(50);
    private List<SqlXMLPO> sqlList = new ArrayList<SqlXMLPO>(50);
    private List<TestPO> testList = new ArrayList<TestPO>(50);
    private List<ServicePO> serviceList = new ArrayList<ServicePO>(50);
    private List<ServiceImplPO> serviceImplList = new ArrayList<ServiceImplPO>(50);
    private SqlPO sqlPO;

    public void init(DaoToolType obj) {

        String filePath = obj.getFilePath();
        String project = obj.getProject();
        String dbname = obj.getDbname();

        // folder
        filePO = new FilePO(filePath);
        // sql
        sqlPO = new SqlPO(filePath, dbname);

        for (ModuleType moduleType : obj.getModules().getModule()) {

            String module = moduleType.getName();

            // package
            packageList.add(new PackagePO(filePath, project, module));

            // moduleModel
            ModuleModelPO mmpo = new ModuleModelPO(filePath, project, module);
            mmList.add(mmpo);

            for (ModelType modelType : moduleType.getModel()) {

                String model = StringMatch.first2Uppercase(modelType.getName());
                String table = modelType.getTable();

                // clazz,dao,service,serviceImpl,ibatis,test,sqlcreate
                clazzList.add(new ClazzPO(filePath, project, module, model));
                daoList.add(new DaoPO(filePath, project, module, model));
                serviceList.add(new ServicePO(filePath, project, module, model));
                serviceImplList.add(new ServiceImplPO(filePath, project, module, model));

                TableBean tb = new TableBean(table);
                sqlPO.tables.add(tb);
                // ibatisList.add(new IbatisPO(filePath, project, module,
                // model));
                // testList.add(new TestPO(filePath, project, module, model));

                // moduleModel
                mmpo.property.add(new PropertyBean(StringMatch.first2Lowercase(model) + "DTO", model + "DTO"));

                // model,sql,form
                ModelPO mpo = new ModelPO(filePath, project, module, model);
                modelList.add(mpo);
                SqlXMLPO spo = new SqlXMLPO(filePath, project, module, model, table);
                sqlList.add(spo);
                FormPO fpo = new FormPO(filePath, project, module, model);
                formList.add(fpo);

                for (PropType prop : modelType.getProp()) {
                    String propName = prop.getName();
                    String propType = prop.getType();
                    boolean propNull = prop.isNull();
                    boolean propUnique = prop.isUnique();
                    String propDefault = prop.getDefault();

                    // model,sql
                    mpo.property.add(new PropertyBean(propName, propType));
                    fpo.property.add(new PropertyBean(propName, propType));
                    spo.property.add(new PropertyBean(propName, propType, propNull));
                    tb.getPbs().add(new PropertyBean(propName, propType, propNull, propUnique, propDefault));

                    // sql
                    if (propName.equals("mark")) {
                        spo.mark = true;
                    }

                    // model
                    if (JavaSQLMatch.isJavaImport(prop.getType())) {
                        mpo.imports.add(JavaSQLMatch.sql2JAVA(prop.getType()));
                    }
                }

                for (FinderType finder : modelType.getFinder()) {
                    String id = finder.getId();
                    String sql = finder.getSql();

                    // sql
                    spo.finder.add(new SqlFinderBean(StringMatch.first2Lowercase(model) + "." + id, sql));
                }
            }
        }
    }

    public List<PackagePO> getPackageList() {
        return packageList;
    }

    public List<ClazzPO> getClazzList() {
        return clazzList;
    }

    public List<ModelPO> getModelList() {
        return modelList;
    }

    public List<DaoPO> getDaoList() {
        return daoList;
    }

    public List<IbatisPO> getIbatisList() {
        return ibatisList;
    }

    public List<SqlXMLPO> getSqlList() {
        return sqlList;
    }

    public List<TestPO> getTestList() {
        return testList;
    }

    public List<ModuleModelPO> getMmList() {
        return mmList;
    }

    public List<FormPO> getFormList() {
        return formList;
    }

    public List<ServicePO> getServiceList() {
        return serviceList;
    }

    public List<ServiceImplPO> getServiceImplList() {
        return serviceImplList;
    }

    public SqlPO getSqlPO() {
        return sqlPO;
    }

    public FilePO getFilePO() {
        return filePO;
    }

}
