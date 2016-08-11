package example.stores;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Spencer Gibb
 */
@Component
public class StoreRepositoryImpl implements StoreRepositoryCustom {

	private RedisOperations redisOperations;

	public StoreRepositoryImpl(@Qualifier("redisTemplate") RedisOperations redisOperations) {
		this.redisOperations = redisOperations;
	}

	@Override
	public List<GeoResult<RedisGeoCommands.GeoLocation<Store>>> findNear(@Param("location") Point location, @Param("distance") Distance distance) {
		GeoResults geoResults = this.redisOperations.opsForGeo().geoRadius("stores_geo", new Circle(location, distance),
				RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
						.includeCoordinates()
						.includeDistance()
						.limit(10));
		List<GeoResult<RedisGeoCommands.GeoLocation<Store>>> results = geoResults.getContent();
		ArrayList<GeoResult<RedisGeoCommands.GeoLocation<Store>>> list = new ArrayList<>(results.size());
		for (GeoResult<RedisGeoCommands.GeoLocation<Store>> store : results) {
			list.add(store);
		}
		return list;
		// return new PageImpl<>(new ArrayList<Store>(), pageable, results.size());
	}
}