# Spring-WebFlux-Security

Spring WebFlux Security using Jwt and Redis

Docker Redis : docker run -d --name redis -p 6379:6379 redis

Authorize : 

Post on path: "/auth/token" using

Body: 
{
	"username":"user",
	"password":"password"
}

Access any endpoint adding on header:
"Authorization, Bearer ${paste token here}" .

Q&A on cristealucian1997@gmail.com
