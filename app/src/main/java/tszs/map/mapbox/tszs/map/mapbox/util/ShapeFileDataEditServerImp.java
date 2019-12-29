package tszs.map.mapbox.tszs.map.mapbox.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class ShapeFileDataEditServerImp {
    /**
     * 查询
     *
     * @param where
     * @return
     */
    public List<SimpleFeature> queryFeature(String shapeFile, String where) throws Exception {
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        List<SimpleFeature> features = new ArrayList<>();
        try {
            ShapefileDataStore sds = (ShapefileDataStore) dataStoreFactory
                    .createDataStore(new File(shapeFile).toURI().toURL());
            sds.setCharset(Charset.forName("GBK"));
            SimpleFeatureSource featureSource = sds.getFeatureSource();

            SimpleFeatureIterator itertor = null;
            if (where != null && !where.trim().equals("")) {
                Filter filter = CQL.toFilter(where);
                itertor = featureSource.getFeatures(filter).features();
            } else {
                itertor = featureSource.getFeatures().features();
            }
            while (itertor.hasNext()) {
                SimpleFeature feature = itertor.next();
                features.add(feature);
            }
            itertor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return features;
    }

    /**
     * 新增
     *
     * @param
     * @return
     * @throws IOException
     * @throws FactoryException
     * @throws NoSuchAuthorityCodeException
     */
    public void addFeatures(String source, List<SimpleFeature> datas, Map<String, Object> paras)
            throws IOException, NoSuchAuthorityCodeException, FactoryException {

        // 创建shape文件对象
        File file = new File(source);
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(ShapefileDataStoreFactory.URLP.key, file.toURI().toURL());
        params.put("create spatial index", Boolean.TRUE);
        ShapefileDataStore ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);

        if (datas == null || datas.size() <= 0) {
            // 创建shape文件结构
            if (paras == null) {
                return;
            }
            String featureType = paras.get("featureType").toString();
            String spatialReference = paras.get("spatialReference").toString();
            List<FieldInfo> fields = (List<FieldInfo>) paras.get("fieldInfo");

            // 定义图形信息和属性信息
            SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
            CoordinateReferenceSystem crs1 = CRS.decode("EPSG:" + spatialReference);
            tb.setCRS(crs1);
            tb.setName("shapefile");
            if (featureType == "Point") {
                tb.add("the_geom", Point.class);
            } else if (featureType == "LineString") {
                tb.add("the_geom", LineString.class);
            } else if (featureType == "Polygon") {
                tb.add("the_geom", Polygon.class);
            }

            for (FieldInfo info : fields) {
                switch (info.getType()) {
                    case Boolean:
                        tb.add(info.getName(), Integer.class);
                        break;
                    case Char:
                        tb.add(info.getName(), String.class);
                        break;
                    case String:
                        tb.add(info.getName(), String.class);
                        break;
                    case Text:
                        tb.add(info.getName(), String.class);
                        break;
                    case Date:
                    case DateTime:
                        tb.add(info.getName(), Date.class);
                        break;
                    case Decimal:
                        tb.add(info.getName(), Double.class);
                        break;
                    case Double:
                        tb.add(info.getName(), Double.class);
                        break;
                    case Single:
                        tb.add(info.getName(), Double.class);
                        break;
                    case UInt:
                    case Int:
                        tb.add(info.getName(), Integer.class);
                        break;
                    case ByteArray:
                        tb.add(info.getName(), Byte[].class);
                        break;
                    case NoDefault:
                        break;
                    default:
                        break;
                }
            }
            ds.createSchema(tb.buildFeatureType());
            ds.setCharset(Charset.forName("GBK"));
            return;
        } else {
            // 写入文件
            writeShape(ds, datas);
        }
    }

    private static void writeShape(ShapefileDataStore newDataStore, List<SimpleFeature> features)
            throws IOException, NoSuchAuthorityCodeException, FactoryException {
        GeometryAttribute geom = features.get(0).getDefaultGeometryProperty();
        Collection<Property> propertys = features.get(0).getProperties();
        CoordinateReferenceSystem crs = geom.getDescriptor().getCoordinateReferenceSystem();
        SimpleFeatureType ftype = createFeatureType(geom.getValue().getClass(), propertys, crs);
        // 创建shapefile元数据
        newDataStore.createSchema(ftype);
        newDataStore.setCharset(Charset.forName("GBK"));

        // 写入数据
        Transaction transaction = new DefaultTransaction("create");
        String typeName = newDataStore.getTypeNames()[0];
        SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);

        if (featureSource instanceof SimpleFeatureStore) {
            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
            SimpleFeatureCollection collection = new ListFeatureCollection(ftype, features);
            featureStore.setTransaction(transaction);
            try {
                featureStore.addFeatures(collection);
                transaction.commit();
            } catch (Exception problem) {
                problem.printStackTrace();
                transaction.rollback();
            } finally {
                transaction.close();
            }
        }
    }

    /**
     * 创建表结构
     *
     * @throws FactoryException
     * @throws NoSuchAuthorityCodeException
     */
    private static SimpleFeatureType createFeatureType(Class<?> geoTypeClass, Collection<Property> propertys,
                                                       CoordinateReferenceSystem crs) throws NoSuchAuthorityCodeException, FactoryException {
        // 定义图形信息和属性信息
        SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
        tb.setCRS(crs);
        tb.setName("shapefile");
        tb.add("the_geom", geoTypeClass);
        for (Property item : propertys) {
            tb.add(item.getName().toString(), item.getType().getBinding());
        }
        // build the type
        final SimpleFeatureType ftype = tb.buildFeatureType();
        return ftype;
    }

    /**
     * 查询
     *
     * @param source
     * @param option
     *            查询的操作(相交:INTERSECT，包含:CONTAINS等)
     * @param geometry
     *            查询空间条件
     * @return
     */
    public List<SimpleFeature> queryFeature(String source, String option, Geometry geometry) throws Exception {
        return null;
    }

    /**
     * 编辑
     *
     * @return
     */
    public SimpleFeature updateFeature(String shapeFile, SimpleFeature row, Map<String, Object> paras) {
        return null;
    }

    /**
     * 删除
     *
     * @return
     */
    public void deleteFeature(String shapeFile, Map<String, Object> paras, String where) {
        if (where == null || where.equals("")) {
            return;
        }
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        try {
            ShapefileDataStore sds = (ShapefileDataStore) dataStoreFactory
                    .createDataStore(new File(shapeFile).toURI().toURL());
            sds.setCharset(Charset.forName("GBK"));
            SimpleFeatureSource featureSource = sds.getFeatureSource();

            // 删除数据
            Transaction transaction = new DefaultTransaction("delete");
            if (featureSource instanceof SimpleFeatureStore) {
                SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
                featureStore.setTransaction(transaction);
                try {
                    if (where != null && !where.trim().equals("")) {
                        Filter filter = CQL.toFilter(where);
                        featureStore.removeFeatures(filter);
                    }
                    transaction.commit();
                } catch (Exception problem) {
                    problem.printStackTrace();
                    transaction.rollback();
                } finally {
                    transaction.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
