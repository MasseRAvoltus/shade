/*
 * Copyright (c) 2012-2017 by its authors. Some rights reserved.
 * See the project homepage at: https://github.com/monix/shade
 *
 * Licensed under the MIT License (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy
 * of the License at:
 *
 * https://github.com/monix/shade/blob/master/LICENSE.txt
 */

package shade.memcached

import net.spy.memcached.ConnectionFactoryBuilder.Locator
import net.spy.memcached.ops.OperationQueueFactory
import net.spy.memcached.{ DefaultConnectionFactory, HashAlgorithm }

import scala.concurrent.duration._

/**
 * Represents the Memcached connection configuration.
 *
 * @param addresses         the list of server addresses, separated by space,
 *                          e.g. `"192.168.1.3:11211 192.168.1.4:11211"`
 * @param authentication    the authentication credentials (if None, then no authentication is performed)
 *
 * @param keysPrefix        is the prefix to be added to used keys when storing/retrieving values,
 *                          useful for having the same Memcached instances used by several
 *                          applications to prevent them from stepping over each other.
 *
 * @param protocol          can be either `Text` or `Binary`
 *
 * @param failureMode       specifies failure mode for SpyMemcached when connections drop:
 *                          - in Retry mode a connection is retried until it recovers.
 *                          - in Cancel mode all operations are cancelled
 *                          - in Redistribute mode, the client tries to redistribute operations to other nodes
 *
 * @param operationTimeout  is the default operation timeout; When the limit is reached, the
 *                          Future responses finish with Failure(TimeoutException)
 *
 * @param timeoutThreshold  is the maximum number of timeouts for a connection that will be tolerated before
 *                          the connection is considered dead and will not be retried. Once this threshold is breached,
 *                          the client will consider the connection to be lost and attempt to establish a new one.
 *                          If None, the default Spymemcached implementation is used (998)
 *
 * @param shouldOptimize    If true, optimization will collapse multiple sequential get ops.
 *
 * @param opQueueFactory    can be used to customize the operations queue,
 *                          i.e. the queue of operations waiting to be processed by SpyMemcached.
 *                          If `None`, the default SpyMemcached implementation (a bounded ArrayBlockingQueue) is used.
 *
 * @param readQueueFactory  can be used to customize the read queue,
 *                          i.e. the queue of Memcached responses waiting to be processed by SpyMemcached.
 *                          If `None`, the default SpyMemcached implementation (an unbounded LinkedBlockingQueue) is used.
 *
 * @param writeQueueFactory can be used to customize the write queue,
 *                          i.e. the queue of operations waiting to be sent to Memcached by SpyMemcached.
 *                          If `None`, the default SpyMemcached implementation (an unbounded LinkedBlockingQueue) is used.
 *
 * @param hashAlgorithm     the method for hashing a cache key for server selection
 *
 * @param locator           locator selection, by default ARRAY_MOD
 */
case class Configuration(
  addresses: String,
  authentication: Option[AuthConfiguration] = None,
  keysPrefix: Option[String] = None,
  protocol: Protocol.Value = Protocol.Binary,
  failureMode: FailureMode.Value = FailureMode.Retry,
  operationTimeout: FiniteDuration = 1.second,
  timeoutThreshold: Option[Int] = None,
  shouldOptimize: Boolean = false,
  opQueueFactory: Option[OperationQueueFactory] = None,
  writeQueueFactory: Option[OperationQueueFactory] = None,
  readQueueFactory: Option[OperationQueueFactory] = None,
  hashAlgorithm: HashAlgorithm = DefaultConnectionFactory.DEFAULT_HASH,
  locator: Locator = Locator.ARRAY_MOD)

object Protocol extends Enumeration {
  type Type = Value
  val Binary, Text = Value
}

object FailureMode extends Enumeration {
  val Retry, Cancel, Redistribute = Value
}

case class AuthConfiguration(
  username: String,
  password: String)
