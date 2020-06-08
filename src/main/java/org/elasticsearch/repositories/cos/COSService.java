package org.elasticsearch.repositories.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.cluster.metadata.RepositoryMetaData;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.repositories.RepositoryException;

public class COSService extends AbstractLifecycleComponent {

    private COSClient client;
    public static final ByteSizeValue MAX_SINGLE_FILE_SIZE = new ByteSizeValue(5, ByteSizeUnit.GB);

    COSService(Settings settings, RepositoryMetaData metaData) {
        super(settings);
        this.client = createClient(metaData);
    }

    private synchronized COSClient createClient(RepositoryMetaData metaData) {
        String access_key_id = COSClientSettings.ACCESS_KEY_ID.get(metaData.settings());
        String access_key_secret = COSClientSettings.ACCESS_KEY_SECRET.get(metaData.settings());
        String region = COSClientSettings.REGION.get(metaData.settings());
        if (region == null || !Strings.hasLength(region)) {
            throw new RepositoryException(metaData.name(), "No region defined for cos repository");
        }
        String endPoint = COSClientSettings.END_POINT.get(metaData.settings());

        COSCredentials cred = new BasicCOSCredentials(access_key_id, access_key_secret);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        if (Strings.hasLength(endPoint)) {
            clientConfig.setEndPointSuffix(endPoint);
        }
        COSClient client = new COSClient(cred, clientConfig);

        return client;
    }

    @Override
    protected void doStart() throws ElasticsearchException {
    }

    @Override
    protected void doStop() throws ElasticsearchException {
    }

    @Override
    protected void doClose() throws ElasticsearchException {
    }

    public COSClient getClient() {
        return this.client;
    }

}
