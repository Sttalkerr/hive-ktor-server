package com.hivestudio.server.profile.repository

import com.hivestudio.server.auth.model.LoginRequest
import com.hivestudio.server.auth.model.RegisterRequest
import com.hivestudio.server.demo.DemoDataFactory
import com.hivestudio.server.domain.model.Producer

class DemoProducerRepository : ProducerRepository {
    override fun getCurrent(): Producer = DemoDataFactory.producer()

    override fun register(request: RegisterRequest): Producer = DemoDataFactory.producer()

    override fun login(request: LoginRequest): Producer = DemoDataFactory.producer()
}
