;; Copyright 2008-2012 Red Hat, Inc, and individual contributors.
;; 
;; This is free software; you can redistribute it and/or modify it
;; under the terms of the GNU Lesser General Public License as
;; published by the Free Software Foundation; either version 2.1 of
;; the License, or (at your option) any later version.
;; 
;; This software is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
;; Lesser General Public License for more details.
;; 
;; You should have received a copy of the GNU Lesser General Public
;; License along with this software; if not, write to the Free
;; Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
;; 02110-1301 USA, or see the FSF site: http://www.fsf.org.

(ns immutant.integs.msg.remote-listen
  (:use fntest.core
        clojure.test
        immutant.messaging))

(def bam-queue "/queue/bam")
(def hiscuit-queue "/queue/hiscuit")

(use-fixtures :once (with-deployment *file*
                      {
                       :root "target/apps/messaging/queues"
                       }))

(deftest remote-listen-should-work
  (listen bam-queue
          (fn [m]
            (publish hiscuit-queue m))
          :host "integ-app1.torquebox.org" :port 5445)
  (publish bam-queue "listen-up")
  (is (= (receive hiscuit-queue :timeout 60000) "listen-up")))
