db.createUser(
        {
            user: "admin",
            pwd: "password",
            roles: [
                {
                    role: "readWrite",
                    db: "ranked_domain"
                }
            ]
        }
);
db.createCollection("ranked_domain_collection", {});
db.getCollection("ranked_domain_collection").createIndex({ "domain" : 1 }, { "unique": true });