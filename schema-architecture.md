This Spring Boot application uses both MVC and REST controllers. Thymeleaf templates are used for the Admin and Doctor dashboards, while REST APIs serve all other modules. The application interacts with two databases—MySQL (for patient, doctor, appointment, and admin data) and MongoDB (for prescriptions). All controllers route requests through a common service layer, which in turn delegates to the appropriate repositories. MySQL uses JPA entities while MongoDB uses document models.

1. User accesses AdminDashboard or Appointment pages.
2. The action is routed to the appropriate Thymeleaf or REST controller.
3. The controller calls the service layer where business rules are applied.
4. The service layer communicates with the repository layer to perform data access operations using MySQL and MongoDB.
5. Each repository interfaces directly with the database. MySQL stores all core entities such as user, roles, and appointments. MongoDB stores flexible data structures, such as prescriptions. 
6. Retrieved data is mapped into java model classes, for MySQL, data is converted into JPA entities, annotated with @Entity. For MongoDB, data is loaded into document objects,  annotated with @Document.
7. Finally models are used in the response layer, in mvc flows, models are passed from the controller to the thymeleaf template. In REST flows, the same models are serialized into json and sent back to the client.