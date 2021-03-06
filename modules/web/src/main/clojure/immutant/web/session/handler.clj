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

(ns immutant.web.session.handler
  (:use [immutant.web.core :only [current-servlet-request]])
  (:require [immutant.utilities :as util]))

(def cookie-encoder (util/try-resolve-any
                     'ring.util.codec/form-encode  ;; ring >= 1.1.0
                     'ring.util.codec/url-encode))

(defn remove-ring-session-cookie
  "Middleware that removes the ring-session cookie if the servlet session is used."
  [handler]
  (fn [request]
    (let [response (handler request)]
      (if-let [session (.getSession current-servlet-request)]
        (update-in response [:headers "Set-Cookie"]
                   #(filter (fn [^String cookie]
                              (not (.contains cookie
                                              (cookie-encoder (.getId session))))) %))
        response))))


