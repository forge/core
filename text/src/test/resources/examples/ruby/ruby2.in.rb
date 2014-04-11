# encoding: utf-8

# lazy enumerations
e = %w{this is a test}.to_enum.with_index
loop { print e.next }
puts

e = (1..Float::INFINITY).lazy.select { |num| num % 5 == 0 && num % 3 == 0}
puts e.take(10).force

puts [1,2,3].to_enum(:each) { 10 }.size

# keyword arguments
def pretend_default_arguments(options = {})
  defaults = {y: 4}
  options = defaults.merge(options)
  p options
end
pretend_default_arguments(:x => 10)
pretend_default_arguments(x:  10, y: 100)

def default_args(x: 10, y: 20)
  p x, y
end
default_args()
default_args(x: 1)

def greedy_star(x:nil, y:nil, **args)
  p x, y
  p args
end
greedy_star c:'test', x:1, y:2

# Module#prepend
module Foo
  def method_name
    "inside the module"
  end
end

class Bar
  prepend Foo
  def method_name
    "inside the class"
  end
end

class Baz
  extend Foo
  def method_name
    "inside the class"
  end
end


x = Bar.new
p x.method_name

x = Baz.new
p x.method_name

# Refinements
module FloatDivision
  refine Fixnum do
    def /(other)
      self.to_f / other
    end
  end
end

class MathFun
  using FloatDivision
  def self.ratio(a, b)
    a/b
  end
end

p MathFun.ratio(6, 8)

# Misc
p nil.to_h
p Hash([])

days = %i{sun mon tue wed thu fri sat}
p days
p %I{ #{1*3} }, %w{jan feb mar}

puts __dir__
puts File.dirname(File.realpath(__FILE__))

warn "debug"

define_method("new_method") do
  puts "hello"
end
new_method()

p Object.const_get("Math::PI")




