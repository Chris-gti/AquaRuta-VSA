package com.aquaruta.domain.strategy;

/**
 * Factoría para la obtención de estrategias de cálculo de riesgo vial.
 *
 * @author AquaRuta VSA Engineering Team
 */
public class RiskStrategyFactory {

    private final NormalWeatherRiskStrategy normalStrategy;
    private final FloodResilientRiskStrategy floodStrategy;

    public RiskStrategyFactory() {
        this.normalStrategy = new NormalWeatherRiskStrategy();
        this.floodStrategy = new FloodResilientRiskStrategy();
    }

    /**
     * Retorna la estrategia adecuada según el modo de simulación o nivel de lluvia global.
     *
     * @param resilientMode Si es verdadero, prioriza la evasión de zonas bajas y anegadas
     * @return Implementación de {@link RiskCalculationStrategy}
     */
    public RiskCalculationStrategy getStrategy(boolean resilientMode) {
        return resilientMode ? floodStrategy : normalStrategy;
    }

    public NormalWeatherRiskStrategy getNormalStrategy() {
        return normalStrategy;
    }

    public FloodResilientRiskStrategy getFloodStrategy() {
        return floodStrategy;
    }
}
