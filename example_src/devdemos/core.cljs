(ns devdemos.core
  (:require
   [devcards.core :as dc :include-macros true]
   [devcards.system :refer [IMount]]   
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]   
   [clojure.string :as string]
   [sablono.core :as sab :include-macros true]
   [devdemos.two-zero])
  (:require-macros
   [devcards.core :refer [defcard is are= are-not= format-code format-data mkdn-code mkdn-data]]))

(enable-console-print!)
(devcards.core/start-devcard-ui!)
(devcards.core/start-figwheel-reloader!
 #_{:websocket-url "ws://localhost:3449/figwheel-ws"})

(defcard intro
  (dc/markdown-card
   "## Devcards

    ClojureScript Devcards are a tool to help you **quickly** surface what
    you are working on. This page holds an quick introduction to
    devcards.

    #### This is a devcard

    This is a **markdown-card**. You can create a markdown-card like
    this like so:"

   (mkdn-code
    (defcard first-markdown-card
      (markdown-card "# This is a heading")))
   
   "Devcards are designed to be written inline with your code during
    development. They are like fancy `println`s that can hold any
    arbitrary functionality that you want." ))

(defcard intro-2
  (dc/markdown-card
   "## Devcards are intended to be interactive

   The cars on this page can be found in the file
   `example_src/devdemos/core.cljs`.Please follow along in this file
   to see how these examples are created.

   If you ran `lein figwheel` to get this demo started, you can edit
   and save the file that this code is in to see the changes
   show up on this page as you save your file.

   Go ahead and change this text to see the changes reflected here."))

(defcard examples-intro
  (dc/markdown-card
   "## Devcard examples"))

(defcard edn-card-example
  (dc/edn-card
   {:edn-card "This is an edn card"
    :helpful? "It lets you quickly view EDN"}))

(defcard sablono-card-example
  (dc/sab-card
   [:div ;; if you want padding
    [:h2 "This is a Sablono card"]
    [:p "It can help you interactively work on sablono templates like this one:"]
    [:pre [:code
           (format-data [:div.devcard-padding ;; if you want padding
                         [:h2 "This is a Sablono card"]
                         [:p "It can help you interactively work on Sablono templates:"]
                         [:pre [:code (format-code ...)]]])
           ]]
    [:p "You can create a sablono card like so:"]
    [:pre [:code
           (format-code
            (defcard sablono-card-example
              (sab-card [:div.devcard-padding ;; if you want padding
                         [:h2 "This is a Sablono card"]
                         [:p "It can help you interactively work on Sablono templates:"]
                         [:pre [:code (format-code ...)]]])))]]
    [:h2 "Working on cards individually"]
    [:p "You will notice that unlike the markdown cards, this card has a heading at the top: "
     [:code "sablono-card-example"]]
    [:p "You can click the heading on this card to get it on a page all to itself."
     " This can reduce the noise of having all the cards on one page."]]))

(defcard react-card-example
  (dc/react-card
   (dom/div #js {}
            (dom/h2 #js {} "This is a react card."))))

(defcard react-runner-card-example
  (dc/react-runner-card
   (fn [data-atom]
     (sab/html [:div
                [:h3 "This is a " "react-runner-card"]
                [:p "This card triggers a re-render when it the atom is modified"]
                [:div "Count: " (:count @data-atom)]
                [:ul
                 [:li [:a {:onClick (fn [] (swap! data-atom update-in [:count] inc))} "inc"]]
                 [:li [:a {:onClick (fn [] (swap! data-atom update-in [:count] dec))} "dec"]]]]))
   { :initial-state {:count 30 } }))

(defn widget [data owner]
  (reify
    om/IRender
    (render [this]
      (sab/html [:h2 "This is an om card, " (:text data)]))))

(defcard omcard-ex
    (dc/om-root-card widget {:text "yozers"}))

(defcard test-card-ex
  (dc/test-card
   "## Test card
    Test cards provide interactive testing inline with your code.
    Test cards allow arbitrary markdown in them, this can allow for a
    literate coding style"
   (is (= 23 (+ 21 2)))
   (are= (+ 3 4 5) 12)
   "`(+ 3 4 5)` is definitely equal to `12`"
   (are-not= (+ 3 4 5) 12)))

(defn rand-strs [c]
  (repeatedly c #(apply str (map (fn [x] (char (rand-int 255)))
                                 (range (rand-int 12))))))

(defcard slider-intro
  (dc/markdown-card
   "## Slider card"
   "A slider card helps you apply ranges of data to a function and see
   the results. It will render the result with the edn-renderer by default.

   If you want to render the data arbitrarily you can provide a
   function that returns a React component to named argument `:value-render-func` "))

(defcard slider-card-dev
  (dc/slider-card (fn [{:keys [x y z] :as state}]
                    (assoc state :result (+ x y z)))
                  {:x (rand-strs 255)
                   :y (rand-strs 255)
                   :z (rand-strs 255)}))

#_(defcard reduce-slider-trans
    (dc/reduce-fr-card
     (dc/SliderCard. 1 1)
     {}
     [:set-index-for-key {:k 0 :index 5}] {:keyed-vals {0 5}}
     [:set-index-for-key {:k 0 :index 3}] {:keyed-vals {0 3}}
   [:set-index-for-key {:k 1 :index 3}] {:keyed-vals {0 3 1 3}}))

(defn css-transform [{:keys [rx ry rz tx ty tz]}]
  (let [trns (str "rotateX(" (or rx 0) "deg) "
                             "rotateY(" (or ry 0) "deg) "
                             "rotateZ(" (or rz 0) "deg) "
                             "translateX(" (or tx 0) "px) "
                             "translateY(" (or ty 0) "px) "
                             "translateZ(" (or tz 0) "px) "
                             )]
    { "-webkit-transform" trns
      "transform" trns }))

(defn side [trans side-css]
  [:div.side {:style (clj->js
                      (merge side-css
                                (css-transform trans)))}])

(defn cube [{:keys [size cube-css side-css]}]
  (let [translate (/ size 2)
        base-side-css { :backgroundColor "green"
                        :width  (str size "px")
                        :height (str size "px")
                        :position "absolute"
                        :top "0px"}
        base-cube-css { :width "100%"
                        :height "100%"
                        :-webkit-transform-style "preserve-3d"}
        cube-css (merge base-cube-css cube-css)
        side-css (merge base-side-css side-css)]
    [:div.cube {:style (clj->js cube-css)}
     (side { :ry 0 :tz translate} (assoc side-css :backgroundColor "blue"))
     (side { :ry 180 :tz translate} (assoc side-css :backgroundColor "blue"))
     (side { :ry 90 :tz translate} (assoc side-css :backgroundColor "green"))
     (side { :ry 270 :tz translate} (assoc side-css :backgroundColor "green"))
     (side { :rx 90 :tz translate} (assoc side-css :backgroundColor "red"))
     (side { :rx 270 :tz translate} (assoc side-css :backgroundColor "yellow"))     ]))

(defn cube-template [state]
  [:div
   {:style (js-obj "position" "relative"
                   "width" "200px"
                   "height" "200px")}
   (cube { :size 200
          :cube-css (css-transform state)
          :side-css {  :width   "200px"
                       :height  "200px"
                       :opacity "0.5"
                       :border "10px solid #333"}})])

(defcard threed-fun
  (dc/slider-card
   identity
   {:rx (range 360)
    :ry (range 360)
    :rz (range 360)}
   :value-render-func cube-template))

(defcard hekler-card-intro
  (dc/markdown-card
   "### Hekler Card
   A heckler card is a primitive quick check to help find bugs in a function.
   You provide a function to test and a generator which generates vectors
   of arguments for that function. "))

(defn to-heckle-f [a b]
  (if (zero? (mod b 10))
    (throw (js/Error. "Crappers Error Thrown"))
    (+ a b)))

(defcard heckler-card-ex
  (dc/heckler-card
   to-heckle-f
   ;; generator
   (fn [] (map vector
              (repeatedly 30 #(rand-int 300))
              (repeatedly 30 #(rand-int 300))))
   :test-func (fn [args x] (< x 350))))


(defcard reduce-card-intro
  (dc/markdown-card
   "## Reduce card
   A **reduce-card** is helpful for testing reduce
   functions. It takes a reduce function, a starting value, a vector of
   arguments, and the expected accumulated results in this form:
   `[arg1 accum1 arg2 accum2 arg3 accum3]`"))

(defcard reduce-card-ex
  (dc/reduce-card + 1 [1 2
                       1 3
                       1 4
                       1 5]))

(defcard creating-your-own-cards
  (dc/markdown-card
   "# Creating your own cards"
   "You can easily create your own cards. There are two ways to create
   devcards; you can simply define a function to be a devcard, or you
   can reify an instance that implements the devcard protocols.

   The devcard system maintains two things for each devcard, an atom
   and a DOM node. The card can then do what it wants, but the idea is
   for all the state to be maintained in the atom and for all the
   rendering to be renderd to the node." ))

(defcard base-api
  (dc/markdown-card
   "## Function API
   Devcard functions are basically functions that take a DOM node and a data atom.

   For example this is a devcard."

   (mkdn-code (fn [{:keys [node data-atom]}] (set! (.-innerHTML node "<h2>Awesome?</h2>"))))

   "And you can use it like so:"

   (mkdn-code (defcard my-first-card
                (fn [{:keys [node data-atom]}]
                  (set! (.-innerHTML node) "<h2>Awesome?</h2>"))))

   "You can see it rendered below."))

(defcard my-first-card
  (fn [{:keys [node data-atom]}]
    (set! (.-innerHTML node) "<h2>Awesome?</h2>")))

(defcard advanced-api
  (dc/markdown-card
   "## The Devcard Protocols API"
   "Creating a devcard with the protocols API looks like this:"
   (mkdn-code
    (defn super-card [initial-state]
      (reify
        IMount
        (mount [_ {:keys [node data-atom]}]
          (render-to (sab/html [:h1 "Super!"]) node))
        IUnMount
        (unmount [_ {:keys [node]}]
          (unmount-react node))
        IConfig
        (-options [_]
          { :unmount-on-reload false
            :initial-state initial-state }))))
   "You can then use the card like so:"
   (mkdn-code
    (defcard my-card-ex (super-card {})))))

