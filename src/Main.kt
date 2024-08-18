
class Swift{
}

class Message{
}

class B2EDto{
}

data class Order(val text: String)

interface IOrderFactory<T>{
    fun createOrder(input: T): Order
}

class MessageOrderFactory: IOrderFactory<Message>{
    override fun createOrder(input: Message): Order{
        return Order(input.javaClass.simpleName)
    }
}

class B2EOrderFactory: IOrderFactory<B2EDto>{
    override fun createOrder(input: B2EDto): Order{
        return Order(input.javaClass.simpleName)
    }
}

class TransferInitiationProcess<T>(private val orderFactory: IOrderFactory<T>, private val preparationProcessor: PreparationProcessor){
    fun process(input: T): Order {
        val order = orderFactory.createOrder(input)
        preparationProcessor.process(order)
        return order
    }}

class PreparationProcessor{
    interface IEnricher{
        fun enrich(order: Order)
    }

    private val processors: List<IEnricher> = mutableListOf()

    fun process(order: Order){
        processors.forEach {
            it.enrich(order)
        }
    }
}

class TransferInitiationProcessFactory{
    private val defaultPreparationProcessor = PreparationProcessor()

    fun <T> createProcess(orderFactory: IOrderFactory<T>): TransferInitiationProcess<T>{
        return TransferInitiationProcess(orderFactory, defaultPreparationProcessor)
    }


}

fun main() {
    val message = Message()
    val messageFactory = MessageOrderFactory()

    val messageProcessor = TransferInitiationProcessFactory()
        .createProcess(messageFactory)



    println("message:" + messageProcessor.process(message).text)

    val b2eDto = B2EDto()
    val b2eFactory = B2EOrderFactory()
    println("b2e:" + TransferInitiationProcessFactory().createProcess(b2eFactory)
        .process(b2eDto).text)
}