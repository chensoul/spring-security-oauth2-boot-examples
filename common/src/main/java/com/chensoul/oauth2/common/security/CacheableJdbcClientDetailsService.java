package com.chensoul.oauth2.common.security;

import com.chensoul.oauth2.common.constants.SecurityConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.util.CollectionUtils;


/**
 * Cacheable Jdbc ClientDetails Service
 *
 * @author <a href="mailto:chensoul.eth@gmail.com">chensoul</a>
 * @since 4.0.0
 */
@Slf4j
public class CacheableJdbcClientDetailsService extends JdbcClientDetailsService {
    private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;

    /**
     * @param dataSource
     * @param redisTemplate
     */
	public CacheableJdbcClientDetailsService(DataSource dataSource, RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(dataSource);
        this.redisTemplate = redisTemplate;
		this.objectMapper = objectMapper;
    }


    /**
     * @param clientId
     * @return {@link ClientDetails}
     * @throws InvalidClientException
     */
	@SneakyThrows
	@Override
    public ClientDetails loadClientByClientId(String clientId) throws InvalidClientException {
        if (StringUtils.isBlank(clientId)) {
            throw new NoSuchClientException("客户端ID不能为空");
        }

        ClientDetails clientDetails = null;

        String clientDetailsValue = (String) redisTemplate.opsForHash().get(SecurityConstants.OAUTH_CLIENT_DETAIL, clientId);
        if (StringUtils.isNotBlank(clientDetailsValue)) {
			clientDetails = objectMapper.readValue(clientDetailsValue, BaseClientDetails.class);
        }

        if (clientDetails == null) {
            clientDetails = super.loadClientByClientId(clientId);
        }

        if (clientDetails != null) {
            updateRedisCache(clientDetails);
        }

        return clientDetails;
    }


    /**
     * @param clientId
     * @throws NoSuchClientException
     */
    @Override
    public void removeClientDetails(String clientId) throws NoSuchClientException {
        super.removeClientDetails(clientId);

        removeRedisCache(clientId);
    }

    /**
     * @param clientDetails
     * @throws NoSuchClientException
     */
    @Override
    public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {
        super.updateClientDetails(clientDetails);

        updateRedisCache(clientDetails);
    }

    /**
     * @param clientId
     * @param secret
     * @throws NoSuchClientException
     */
    @Override
    public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {
        super.updateClientSecret(clientId, secret);

        ClientDetails clientDetails = super.loadClientByClientId(clientId);
        if (clientDetails != null) {
            BaseClientDetails baseClientDetails = (BaseClientDetails) clientDetails;
            baseClientDetails.setClientSecret(NoOpPasswordEncoder.getInstance().encode(secret));
            updateRedisCache(clientDetails);
        }
    }

    /**
     * @return {@link List}<{@link ClientDetails}>
     */
    @Override
    public List<ClientDetails> listClientDetails() {
        List<ClientDetails> clientDetails = super.listClientDetails();
        loadAllClientToCache(clientDetails);

        return clientDetails;
    }


    /**
     * @param clientId
     */
    public void removeRedisCache(String clientId) {
        redisTemplate.opsForHash().delete(SecurityConstants.OAUTH_CLIENT_DETAIL, clientId);
    }


    /**
     * @param clientDetails
     */
	@SneakyThrows
	public void updateRedisCache(ClientDetails clientDetails) {
		redisTemplate.opsForHash().put(SecurityConstants.OAUTH_CLIENT_DETAIL, clientDetails.getClientId(), objectMapper.writeValueAsString(clientDetails));
    }

    /**
     * @param clientDetails
     */
    public void loadAllClientToCache(List<ClientDetails> clientDetails) {
        if (redisTemplate.hasKey(SecurityConstants.OAUTH_CLIENT_DETAIL)) {
            return;
        }
        if (CollectionUtils.isEmpty(clientDetails)) {
            return;
        }
        clientDetails.forEach(client -> updateRedisCache(client));
    }

}
