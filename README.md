**Spring Data + Hibernate Search**

We're following features from similar Elasticsearch implementation
* https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/

e.g.

    List<SimpleEntity> findByLocationWithin(double latitude, double longitude, double distance);
    List<SimpleEntity> findByNameNot(String notName, Sort sort);
    List<SimpleEntity> findByNumberBetween(int min, int max, Sort sort);

Usage:    
    
    @Test
    public void testOps() {
        assertSize(repository.findByLocationWithin(24.0, 31.5, 55), 1);
        assertIds(repository.findByNameNot("doug", Sort.by(Sort.Direction.DESC, "hero")), 1, 2, 5, 3, 6);
        assertIds(repository.findByNumberBetween(-5, 11, Sort.by("hero")), 4, 3);
    }
