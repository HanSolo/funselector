/*
 * Copyright (c) 2021 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.fx.funselector;

import javafx.animation.Interpolator;


/**
 * User: hansolo
 * Date: 06.06.21
 * Time: 05:03
 */
public class SpringInterpolator extends Interpolator {
    // the amplitude of the wave
    // controls how far out the object can go from it's final stopping point.
    private final double amplitude;

    // determines the weight of the object
    // makes the wave motion go longer and farther
    public final double mass;

    // the stiffness of the wave motion / spring
    // makes the motion shorter and more snappy
    public final double stiffness;

    // makes the wave motion be out of phase, so that the object
    // doesn't end up on the final resting spot.
    // this variable is usually never changed
    public final double phase;

    // if this should do a normal spring or a bounce motion
    public final boolean bounce;

    // internal variables used for calcuations
    private double pulsation;


    // ******************** Constructors **************************************
    public SpringInterpolator() {
        this(1.0, 0.058, 12.0, 0.0, false);
    }
    public SpringInterpolator(final double amplitude, final double mass, final double stiffness, final double phase, final boolean bounce) {
        this.amplitude = amplitude;
        this.mass      = mass;
        this.stiffness = stiffness;
        this.phase     = phase;
        this.bounce    = bounce;
        pulsation      = Math.sqrt(this.stiffness / this.mass);
    }


    // ******************** Spring equation ***********************************
    @Override protected double curve(final double t) {
        double t2 = -Math.cos(pulsation * t + phase + Math.PI) * (1 - t) * amplitude;
        return bounce ? 1 - Math.abs(t2) : 1 - t2;
    }
}
