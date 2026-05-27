# PubSub Java Library

Sistema de publicação/assinatura (Pub/Sub) de eventos assincronos desacoplado e extensível para aplicações Java, baseado em EventBus.

## ✨ Destaques

- Comunicação desacoplada entre componentes via eventos.
- Encadeamento e consumo seguro de eventos.
- Gerenciamento centralizado de exceções.
- Carregamento dinâmico de assinantes.
- Cobertura de testes automatizados com JUnit 5.

## 🚀 Exemplos de Uso

### 1. Definindo um Evento
```java
public class UserCreatedEvent extends EventMessage {

    private final String username;

    public UserCreatedEvent(String username) {
        this.username = username;
    }

    public String getUsername() { 
        return username; 
    }
}
```

### 2. Criando um Assinante
```java
public class UserEventListener implements EventSubscriber {

    @Subscribe
    public void onUserCreated(UserCreatedEvent event) {
        System.out.println("Novo usuário: " + event.getUsername());
        event.consume(); // Marca o evento como consumido
    }
}
```

### 3. Publicando um Evento
```java
EventPublisher publisher = new EventPublisher();
UserEventListener listener = new UserEventListener();
publisher.subscribe(listener);
publisher.publish(new UserCreatedEvent("samuel"));
publisher.unsubscribeAll();
```

## 📋 Como adicionar ao seu projeto

### Maven
```xml
<dependency>
    <groupId>br.com.samuel.paullo</groupId>
    <artifactId>pubsub</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle
```groovy
dependencies {
    implementation 'br.com.samuel.paullo:pubsub:1.0.0'
}
```

## 💡 Casos de uso
- Sistemas modulares que exigem comunicação desacoplada.
- Frameworks ou bibliotecas que desejam oferecer extensibilidade via eventos.
- Aplicações desktop, mobile ou backend que precisam de eventos customizados.

## 🤝 Contribua
Sugestões, issues e pull requests são bem-vindos!

---
Desenvolvido por Samuel Paulo.
