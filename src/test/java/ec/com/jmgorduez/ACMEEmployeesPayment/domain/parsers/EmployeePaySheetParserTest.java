package ec.com.jmgorduez.ACMEEmployeesPayment.domain.parsers;

import ec.com.jmgorduez.ACMEEmployeesPayment.dataGenerator.TestDataGenerator;
import ec.com.jmgorduez.ACMEEmployeesPayment.domain.EmployeePaySheet;
import ec.com.jmgorduez.ACMEEmployeesPayment.domain.abstractions.IPayable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ec.com.jmgorduez.ACMEEmployeesPayment.dataGenerator.TestDataGenerator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmployeePaySheetParserTest {

    private EmployeePaySheetParser employeePaySheetParserUnderTest;
    private Queue<IPayable> payables;

    @BeforeEach
    void setUp() {
        this.employeePaySheetParserUnderTest = new EmployeePaySheetParser(this::getWorkingTimes,
                TestDataGenerator::numberOfHours);
        this.payables = Stream.of(MO_10_00_12_00, TH_12_00_14_00, SU_20_00_21_00)
                .collect(Collectors.toCollection(ArrayDeque::new));
    }

    @Test
    void parseEmployeePaySheet() {
        EmployeePaySheet.Builder builder = new EmployeePaySheet.Builder(ASTRID)
                .numbersOfUnitsOfTimeWorked(TestDataGenerator::numberOfHours);
        payables.stream().forEach(builder::addWorkingTime);
        EmployeePaySheet employeePaySheetExpected = builder.build();
        assertThat(employeePaySheetParserUnderTest.parseEmployeePaySheet(
                ASTRID_MO_10_00_12_00_TH_12_00_14_00_SU_20_00_21_00))
                .isEqualToIgnoringGivenFields(employeePaySheetExpected,
                        GET_NUMBERS_OF_UNITS_OF_TIME_WORKED);
    }

    @Test
    void parseInvalidEmployeePaySheet() {
        payables.clear();
        assertThatThrownBy(() -> employeePaySheetParserUnderTest.parseEmployeePaySheet(getClass().getName()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getName() {
        assertThat(employeePaySheetParserUnderTest.getName(ASTRID_MO_10_00_12_00_TH_12_00_14_00_SU_20_00_21_00))
                .isEqualTo(ASTRID);
    }

    @Test
    void getWorkingTimes() {
        assertThat(employeePaySheetParserUnderTest.getWorkingTimes(ASTRID_MO_10_00_12_00_TH_12_00_14_00_SU_20_00_21_00))
                .contains(MO_10_00_12_00_STRING, TH_12_00_14_00_STRING, SU_20_00_21_00_STRING);
    }

    private IPayable[] getWorkingTimes(String value) {
        return new IPayable[]{payables.poll()};
    }
}